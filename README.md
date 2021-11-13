# LogAnalyzerApplication. Follow below steps to run application on your computer.
1.In Eclipe SprintToolSuite, open git perspective: Window->Perspective->Open Perspective->Other->Git
2.Clone git repository -> https://github.com/kiranvikas/LogAnalyzerApplication.git
3.Swich the perspective to Java.
4.Import the project: File -> Import->Maven ->Existing Maven Projects->Go to location where the clone repo is cloned and select pom.xml
5 Create Run configuration with goal -> clean spring-boot:run
6.Access the application using url -> http://localhost:8085/loganalyzer/api/v1/logfile?filename=logfile.txt
