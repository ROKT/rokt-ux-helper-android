# Release steps

## Full release

1. Run the workflow called "Create draft release" which will:
    - Generate a pre-release build for you to verify and test
    - Update changelog - unreleased section moved to correct version number
    - Open a PR targeting main branch
2. Once tested and approved by the relevant owners, merge the PR to main
3. Once merged the following will occur:
    - Build uploaded to Maven Central
    - Release made on Github with relevant build files
    - Commit tagged with version number
