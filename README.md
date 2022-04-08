# simBench2pdsm
[![Build Status](https://travis-ci.org/ie3-institute/simbench4ie3.svg?branch=master)](https://travis-ci.org/ie3-institute/simbench4ie3)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/793affe18cd44718a66b07b2a7c45258)](https://www.codacy.com/gh/ie3-institute/simBench2psdm/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ie3-institute/simBench2psdm&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/ie3-institute/simBench2psdm/branch/master/graph/badge.svg)](https://codecov.io/gh/ie3-institute/simBench2psdm)
[![License](https://img.shields.io/github/license/ie3-institute/simbench4ie3)](https://github.com/ie3-institute/simbench4ie3/blob/master/LICENSE)

[![simbench logo](https://simbench.de/wp-content/uploads/2019/01/logo.png  "SimBench Logo")](https://www.simbench.net)

Tool to convert SimBench data sets to ie3's [power system data model](https://github.com/ie3-institute/PowerSystemDataModel).
SimBench has been research project of Kassel university, Fraunhofer IEE, RWTH Aachen university and TU Dortmund university, aiming at provision of realistic models of electrical distribution grids including time series.
The data is availabe at https://simbench.de.

There is another conversion tool available [here](https://github.com/e2nIEE/simbench), that makes the models available to the well known [pandapower](https://github.com/e2nIEE/pandapower) simulation software.

## Hints for usage
If you intend to convert bigger models, like `1-MVLV-rural-all-...`, it may consume a considerable amount of resources.
Therefore, you have two options to make conversion more convenient.
If you don't need time resolved grid usage patterns, you can turn off their conversion by `conversion.createTimeSeries = true` within the config.

If you are interested in obtaining the time series, it is recommended to set some JVM options.
Of course, adapting the heap size is advisory.
For above-mentioned grid, `-Xmx20g` was sufficient, if the config was chosen as follows.
Of course this will also be dependend on your system.
```hocon
conversion.participantWorkersPerType = 5
io.output.workers = 30
```
If running the conversion with Java 8, it is also advisory to define a specific garbage collector to use.
The G1 garbage collector `-XX:+UseG1GC` was a good choice for above-mentioned example application.
