# growatt-kotlin-sdk
Growatt Api Kotlin SDK

An attempt to replicate the data and functionality of https://server.growatt.com/ using Kotlin.

## CI/CD

This project uses GitHub Actions for continuous integration and publishing to Maven Central.

### Workflows

- **Pull Requests**: Runs build and tests on every pull request
- **Release**: Publishes to Maven Central when a GitHub release is created

### Required Secrets for Publishing

To publish to Maven Central, configure these secrets in your GitHub repository settings:

- `SONATYPEUSERNAME`: Your Sonatype OSSRH username
- `SONATYPEPASSWORD`: Your Sonatype OSSRH password or token
- `GPG_KEY`: Your GPG private key in ASCII-armored format (export with `gpg --armor --export-secret-keys YOUR_KEY_ID`)
- `GPG_KEY_PASS`: Your GPG key passphrase

### Publishing a Release

1. Create a new release on GitHub with a tag (e.g., `v0.0.1`)
2. The release workflow will automatically build and publish to Maven Central
