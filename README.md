# ClassFactory integration form PhpStorm

![Build](https://github.com/ekvedaras/class-factory-phpstorm/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/19824.svg)](https://plugins.jetbrains.com/plugin/19824)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/19824.svg)](https://plugins.jetbrains.com/plugin/19824)

<!-- Plugin description -->
PhpStorm integration for [ekvedaras/class-factory](https://github.com/ekvedaras/class-factory) package.

Current features:

* Autocompletion in class factory methods
* Reference resolving for properties in factories. ⚠️ Renaming private promoted properties does not work due to [a bug in PhpStorm](https://youtrack.jetbrains.com/issue/WI-68507/Field-reference-resolved-to-private-property-doesnt-get-renamed)
* Inspection to detect properties that don't exist in targeted class constructor
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "class-factory-phpstorm"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/ekvedaras/class-factory-phpstorm/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
