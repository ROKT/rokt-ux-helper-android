# Release steps

## Full release

1. Update the contents of the file called VERSION with the appropriate new version number
2. Open a PR targeting the main branch which will (WIP)
    - Generate a pre-release build to verify and test
    - Update changelog - unreleased section moved to correct version number
3. Once tested merge the PR to main
4. Once merged the following will occur:
    - Build uploaded to Maven Central
    - Release made on Github with relevant build files
    - Commit tagged with version number
