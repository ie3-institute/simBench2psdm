# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Added Bao and Staudt to list of reviewers [#277](https://github.com/ie3-institute/simBench2psdm/issues/277)
- Added semantic versioning for GHA [#324](https://github.com/ie3-institute/simBench2psdm/issues/324)
- Implemented GitHub Actions Pipeline [#309](https://github.com/ie3-institute/simBench2psdm/issues/309)
- Updated Gradle to new version 9.2.1 [#346](https://github.com/ie3-institute/simBench2psdm/issues/346)

### Changed
- Redesigned the `ExtractorSpec.scala`, to clear test files between different tests. [#299](https://github.com/ie3-institute/simBench2psdm/issues/299)
- Fixed Version Check logic for Dependabot PRs [#330](https://github.com/ie3-institute/simBench2psdm/issues/330)
- Removed Jenkins Pipeline. Now using GitHub Actions [#328](https://github.com/ie3-institute/simBench2psdm/issues/328)
- Switch to Java 21 [#343](https://github.com/ie3-institute/simBench2psdm/issues/343)
- Updated to `scala3` [#313](https://github.com/ie3-institute/simBench2psdm/issues/313)
- Update Authors file [#365](https://github.com/ie3-institute/simBench2psdm/issues/365)

### Added
- Option to use local files instead of download [#256](https://github.com/ie3-institute/simBench2psdm/issues/256)


## [1.0.0] - 2021-08-03
### Added
- Basic functionality to convert SimBench data sets to [PowerSystemDataModel](https://github.com/ie3-institute/powersystemdatamodel)
- Added an Extractor to preprocess and handle the new download link from Uni Kassel, enabling seamless processing of the simbench_datalinks.csv file [#267](https://github.com/ie3-institute/simBench2psdm/issues/267)

[Unreleased]: https://github.com/ie3-institute/simbench2psdm/compare/v1.0...HEAD
[1.0.0]: https://github.com/ie3-institute/simbench2psdm/releases/tag/1.0
