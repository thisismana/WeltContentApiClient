Changelog
=========

0.10.0 (2017-02-03)

**Changes:**

- `PressedSectionService` now wraps its responses
  - the wrapper contains information about `time`, `status`, `source`, .. of the pressed result
  - those wrapped models are uploaded to S3
  - alternate treatment of backend calls when in `Dev Mode` 
- Changed logging to typesafe's scala-logging (should increase performance and reduce memory)
- minor fixes like typos  

**Breaking:**

- the models in S3 and the `PressedSectionService` are now wrapped (JSON changed)

**Migration:**

- the Logger automatically does the `log.isLevelEnabled()` and therefore permits calling this directly. Just remove the `if`.
- As for the wrapped JSON: You could simply unwrap it to use the previous models.

0.9.0 (2017-01-26)

**Changes:**

- use Guice directly instead of Play's DI abstraction.

**Breaking:**

- Play's DI and Guice are compatible to some extend, but you should consider using Guice's interfaces if you access the WC/AC DI-Modules directly 

**Migration:**

Follow DI as described here https://www.playframework.com/documentation/2.5.x/ScalaDependencyInjection

- migrate your code from `play.api.inject.Module` to `com.google.inject.AbstractModule`
- change from `def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]]
` to `protected abstract void configure()`
- for module-dependencies, use `install(m: Module)`

0.8.0 (2017-01-25)
------------------

**Changes:**

- migrated to wrapped search endpoint that allows paginated searches

**Breaking:**

- none, if migration is applied

**Migration:**

- just call `searchResponse.map(_.results)` to unwrap the search result and use the old models
- update the _search_ `endpoint` in your configuration to `/content/wrapped/_search`



0.7.0 (2017-01-13)
------------------

**Changes:**

- params for the `AbstractService` are getting trimmed now (remove leading and trailing white space) Fixes #42

**Breaking:**

- type safe rewrite of the `ApiContentSearch`
- made the `RequestHeader` non `Optional`, but initialize with `Seq.empty` instead

0.4.0 (2016-10-18)
------------------

Bug fixes:

- added missing default case to `ApiChannel`

Features:

- `ContentApiQuery`
  - `flags` will be *deprecated* in favour of the new search parameter `flag`: In our API we currently only have two flags: `highlight` and `premium` and the negation of those. With the new `flag` search parameter you can combine/exclude those flags, e.g. `flag: [premium, no_highlight]` or `flag: [no_premium, no_highlight]`. The results are a conjunction of the flags (the terms are *and*ed -> `!premium && !highlight`.
  - `subTypeExcludes` (some of them formerly available through the `flags`) are moving into the `subType` search parameter, use the negated version of the corresponding subType
  - `subType` search parameter will now allow list inputs, allowing you to do things as `subType: [no_broadcast, no_video, no_printImport, no_oembed]`

version <= 0.3.x
----------------

- initial version