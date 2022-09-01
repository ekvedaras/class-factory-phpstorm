<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# class-factory-phpstorm Changelog

## [Unreleased]
### Added
- Complete properties for attributes array keys in closure states.
- Resolve references for attributes array keys in closure states.
- Detect unknown properties in state and make methods both inside and outside factory.
- Detect unknown properties in attributes array keys in closure states.

## [1.0.0]
### Added
- Initial release with autocompletion, reference resolving and property not found inspection.
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

### Fixed
- `NullPointerException` cast (#1)