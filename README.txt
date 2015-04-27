There are two ways of testing and running both of EMS:

1) Through simapi.ucd.ie website, so you can call its API in order to control the simulation

2) Or the simulation environment should be set up in the following way:

-> Download and install Energy+
-> Download and install BCVTB
-> Finally SimAPI database and local server names should be correct

For both cases the following should be to take care of:

-> Install all necessary packages and libraries that are necessary for HTTP connections, R interface and others
-> Make sure that for Smart Controller a location of files that contain machine learning models should be correctly specified.

* If simulation instance number does not work then, you need to check with the SimApi database.

* ECLIPSE USERS::

Before using the R interface, some settings were required in Eclipse:
1) Need to download and include JRI.jar as external library for the project within Eclipse.
JRI.jar can be downloaded from rforge’s website. After downloading the jar file, it can be included in the project as an external archive.

2) The following paths should be added to the Environment in Run Configurations of Eclipse:
C:\Program Files\R\R-3.1.3\library\rJava\jri\x64; C:\Program Files\R\R-3.1.3\bin\x64