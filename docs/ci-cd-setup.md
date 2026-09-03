# Backend CI/CD setup

Replaces the manual `./mvnw package` + `eb deploy` flow with
`.github/workflows/ci-deploy.yml`.

## Why this exists

`.elasticbeanstalk/config.yml` sets `deploy.artifact` to a pre-built jar, so
`eb deploy` uploads whatever file is sitting in `target/` — it never consults
git. A jar built days earlier deploys successfully and reports success while
shipping the wrong code. This happened. The workflow removes that possibility:
the jar is built from the checked-out commit in the same run that deploys it,
and the final step asserts the version now live in Elastic Beanstalk is the one
just built.

## What runs when

| Event | Build + test | Deploy |
|---|---|---|
| Pull request into `main`/`dev` | yes | no |
| Push to `dev` | yes | no |
| Push to `main` | yes | **after manual approval** |
| Manual run (`workflow_dispatch`) on `main` | yes | **after manual approval** |

Deploy is a separate job gated on the `production` GitHub Environment. It only
runs after `build` passes, and only from `main`.

---

## Required setup (three steps — the deploy job fails without all three)

### 1. Create the `production` environment — this is the approval gate

**Settings → Environments → New environment → `production`**, then tick
**Required reviewers** and add whoever is allowed to release.

> Without this the workflow still runs, but deploys become fully automatic on
> every push to `main`. The gate is the environment, not the workflow file.

### 2. Add the AWS credentials as repository secrets

**Settings → Secrets and variables → Actions**

| Secret | Value |
|---|---|
| `AWS_ACCESS_KEY_ID` | access key of the deploy IAM user |
| `AWS_SECRET_ACCESS_KEY` | its secret key |

No application secrets are needed. Production reads its configuration from
Elastic Beanstalk environment variables and SSM Parameter Store through the
instance profile (`application-prod.properties`), so CI never handles the
database password, the JWT key, or anything else the app runs on.

### 3. Create the deploy IAM user

An IAM user with programmatic access and this policy.

`elasticbeanstalk:CreateStorageLocation` is not needed — the workflow
references the existing bucket directly (`EB_S3_BUCKET` in `ci-deploy.yml`)
rather than resolving it via that call.

`elasticbeanstalk:UpdateEnvironment` performs its own "ensure the artifact
bucket is correctly configured" reconciliation on every deploy, using the
deploying user's own credentials rather than an EB service role — confirmed by
`InsufficientPrivileges` errors naming the deploy IAM user directly. This
surfaced incrementally across the first real deploys: `s3:CreateBucket` first,
then `s3:PutBucketOwnershipControls`. There is no AWS-documented exhaustive
list of what this reconciliation may call (observed elsewhere: bucket policy,
lifecycle, public-access-block, versioning, encryption), and manual deploys
never hit this because a personal AWS user with broad permissions silently
covers all of it.

Rather than discovering the rest one failed deploy at a time, the policy grants
`s3:*` — but scoped to only this one bucket's ARN, nothing account-wide, no
other bucket reachable. Verified with `simulate-principal-policy`: every
bucket-config action tried so far (and several not yet seen) is allowed on this
bucket, the same actions are denied against a different bucket name, and EB
actions remain limited to the five listed here — no `TerminateEnvironment`, no
IAM, no EC2.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ElasticBeanstalkDeploy",
      "Effect": "Allow",
      "Action": [
        "elasticbeanstalk:CreateApplicationVersion",
        "elasticbeanstalk:UpdateEnvironment",
        "elasticbeanstalk:DescribeEnvironments",
        "elasticbeanstalk:DescribeApplicationVersions",
        "elasticbeanstalk:DescribeEvents"
      ],
      "Resource": "*"
    },
    {
      "Sid": "ArtifactBucketFull",
      "Effect": "Allow",
      "Action": "s3:*",
      "Resource": [
        "arn:aws:s3:::elasticbeanstalk-ap-southeast-1-119306256305",
        "arn:aws:s3:::elasticbeanstalk-ap-southeast-1-119306256305/*"
      ]
    }
  ]
}
```

If `update-environment` is rejected, the environment likely needs additional
permissions to touch its own CloudFormation/AutoScaling resources. The AWS
managed policy `AdministratorAccess-AWSElasticBeanstalk` is the documented
fallback — broader, but scoped to Elastic Beanstalk.

**These are long-lived credentials.** Rotate them periodically. GitHub OIDC
removes them entirely and is worth moving to later.

---

## Notes on how it works

**Postgres in CI.** `KfdBackendApplicationTests` is a `@SpringBootTest` with no
H2 or Testcontainers fallback — it boots the real context and runs all 28 Flyway
migrations. Those migrations use Postgres-specific `jsonb` columns, so an
embedded database cannot stand in. The build job runs a `postgres:16` service
container and points the app at it with `SPRING_DATASOURCE_*` environment
variables, which override the committed `application.properties` without editing
it. A useful side effect: every push now proves the migration chain applies
cleanly from an empty database.

**Bare jar, not a zip bundle.** EB is on the Java SE (Corretto 21) platform and
today receives a bare jar. The workflow uploads a bare jar too. Sending a zip
would start applying `.ebextensions/` and `Procfile`, which are *not* part of
the current deploy — possibly a good change, but a behavioural one that should
be made deliberately and separately.

**Version labels** are `gh-<run number>-<short sha>`, so what is running in EB
is traceable to a commit, and rolling back is selecting an earlier version in
the EB console.

**Rollback.** EB keeps previous application versions; redeploy one from the
console. Note that Flyway migrations are forward-only with no down-scripts, so
rolling *code* back does not roll the *schema* back. Schema changes need the
expand/contract pattern to stay reversible.

## Still worth doing

- **Real tests.** The suite is one `contextLoads()`. CI runs whatever gate you
  give it; right now that gate proves the app starts and the migrations apply.
- **A staging environment.** There is only `kfd-backend-prod`. A second
  environment fed by `dev` is what would make fully automatic prod deploys safe
  enough to drop the approval gate.
