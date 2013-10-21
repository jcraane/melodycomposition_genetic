To run UI version of the melody composition application
1. Unzip the melody-composition-<VERSION>.zip to a directory.
2. Make sure the JAVA_HOME variable points to a 1.6 JDK or above
3. Make sure the GROOVY_HOME variable points to groovy 1.6.3 or higher
3. Run the following command from the <PATH_TO_UNZIPPED_ARCHIVE>/classes directory:
java -cp .;../libs/jgap.jar;../libs/miglayout-3.7-swing.jar;../libs/log4j.jar;../libs/groovy-1.6.3.jar;../libs/asm-2.2.3.jar;../libs/melodycomposition-client-2.1.0.jar;../libs/melodycomposition-bundledplugins.jar nl.jamiecraane.melodygeneration.ui.MainApp
