# Double Tap to the Max app for Hubitat

[![CI](https://github.com/joelwetzel/DoubleTapToTheMax/actions/workflows/ci.yml/badge.svg)](https://github.com/joelwetzel/DoubleTapToTheMax/actions/workflows/ci.yml)

If you don't want to wait and hold a dimmer paddle while it raises or lowers, this app lets you go to max or min brightness with double-taps.  It was created so that I wouldn't have to create individual Rule Machine rules for every dimmer in my house.  It takes advantage of how most dimmer drivers report double-tapping.  Generally, double-tap up is reported as button 1, and double-tap down is button 2.

## Installation

The best way to install this code is by using [Hubitat Package Manager](https://community.hubitat.com/t/beta-hubitat-package-manager).

However, it can easily be installed manually.

## Developer Instructions

This project includes a comprehensive integration test suite to ensure code quality and reliability.

### Testing Framework

The tests use biocomp's [Hubitat_CI](https://github.com/biocomp/hubitat_ci) project for integration testing.

### Running Tests

To run the integration tests:

```bash
./gradlew test
```

The test reports can be found in the `build/reports/tests/test/` directory after running the tests.

### Continuous Integration

This repository uses GitHub Actions to automatically run tests on every push and pull request. The CI workflow:
- Runs all integration tests
- Generates test reports
- Uploads test results as artifacts

You can view the CI status and test results in the [Actions tab](https://github.com/joelwetzel/DoubleTapToTheMax/actions).
