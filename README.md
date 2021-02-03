![Maven Central](https://img.shields.io/maven-central/v/it.tidalwave.solidblue3/solidblue3.svg)
[![Build Status](https://img.shields.io/jenkins/s/http/services.tidalwave.it/ci/job/SolidBlue3_Build_from_Scratch.svg)](http://services.tidalwave.it/ci/view/SolidBlue3)
[![Test Status](https://img.shields.io/jenkins/t/http/services.tidalwave.it/ci/job/SolidBlue3.svg)](http://services.tidalwave.it/ci/view/SolidBlue3)
[![Coverage](https://img.shields.io/jenkins/c/http/services.tidalwave.it/ci/job/SolidBlue3.svg)](http://services.tidalwave.it/ci/view/SolidBlue3)

SolidBlue III
================================

A tool for data backup and consistency.

SolidBlue III requires and is tested with JDKs in this range: [17, 18).
It is released under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).

Please have a look at the [project website](https://tidalwave.bitbucket.io/solidblue3j/) for a quick introduction with samples, tutorials, JavaDocs and build reports.


Bootstrapping
-------------

In order to build the project, run from the command line:

```shell
mkdir solidblue3
cd solidblue3
git clone https://bitbucket.org/tidalwave/solidblue3j-src .
mvn -DskipTests
```

The project can be opened with a recent version of the [IntelliJ IDEA](https://www.jetbrains.com/idea/), 
[Apache NetBeans](https://netbeans.apache.org/) or [Eclipse](https://www.eclipse.org/ide/) IDEs.


Contributing
------------

Pull requests are accepted via [Bitbucket](https://bitbucket.org/tidalwave/solidblue3j-src) or [GitHub](https://github.com/tidalwave-it/solidblue3j-src). There are some guidelines which will make 
applying pull requests easier:

* No tabs: please use spaces for indentation.
* Respect the code style.
* Create minimal diffs — disable 'on save' actions like 'reformat source code' or 'organize imports' (unless you use the IDEA specific configuration for 
  this project).
* Provide [TestNG](https://testng.org/doc/) tests for your changes and make sure your changes don't break any existing tests by running
```mvn clean test```. You can check whether there are currently broken tests at the [Continuous Integration](http://services.tidalwave.it/ci/view/SolidBlue3) page.

If you plan to contribute on a regular basis, please consider filing a contributor license agreement. Contact us for more information.


Branch policy
-------------

* Each new feature must be associated to a Jira issue and developed in a branch named `feature/<issue name>`.
* Each bug must be associated to a Jira issue and developed in a branch named `bugfix/<issue name>`.
* Each new release must be prepared in a branch named `release/<version>`, where feature/fix contributions will be merged
  from their specific branche.
* Just before performing a new release the release branch is merged to the `master` branch.


Additional Resources
--------------------

* [Issue tracking](http://services.tidalwave.it/jira/browse/SLB3J)
* [Continuous Integration](http://services.tidalwave.it/ci/view/SolidBlue3)
* [Tidalwave Homepage](http://tidalwave.it)
