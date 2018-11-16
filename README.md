# Undercamber

This is the Undercamber&trade; test framework for Java.  It is a unique approach to testing, with these features:
   - An automatically generated hierarchical GUI for quickly selecting subsets of tests to be run, and fully automated testing without a GUI.
   - Simple programming via a traditional API to support unique or unusual testing strategies,
   - Powerful and simple support for running a test's prerequisites, including skipping setups that have already been performed prior to execution (such as from a previous run), and
   - Support for testing in multiple different JVMs in a single run.

Undercamber was built to address frustration with existing test systems.  In particular, it addresses these issues:
   - Other systems require implementing and tuning byzantine annotations.
   - Running an ad-hoc subset of the tests in other frameworks requires either re-programming the tests or a mind-meld with the annotations.
   - In other systems, adding and removing tests can require tuning and re-tuning annotations.
   - In other systems, setting up prerequisites for each test can be tricky, and it can be difficult to skip prerequisites that have been satisified prior to execution.
   - Other systems assume certain testing strategies, and it is difficult to implement a strategy not anticipated by the framework.
   - When using other systems it can be difficult to test on multiple different JVMs.
   - In many other systems the order of execution is not well defined.
   - There can be a long learing curve with other test frameworks.

The architecture of Undercamber&trade; addresses all of these issues and others with a unique but surprisingly simple approach.

To begin using Undercamber&trade;:
   (1) Download UndercamberUsersGuide.docx
   (2) Download Undercamber.jar (<b>Note:</b> it is not a stand-alone JAR file; see the User's Guide).
   (3) Run the examples in the User's Guide

The Undercamber&trade; test framework is provided free of charge by Rygaard Technologies, LLC. under the BSD 3-clause license.

&quot;Undercamber&quot; is a trademark of Rygaard Technologies, LLC.
