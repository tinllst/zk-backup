### How to package
mvn clean package -DskipTests=true

### How to use
--help
~~~~
option '--help'
export: java -jar zk-backup.jar -export 'zkPath' 'filePath'
import: java -jar zk-backup.jar -import 'filePath'
~~~~
-export
~~~~
e.g. java -jar zk-backup.jar -export /basis ./
~~~~
-import
~~~~
e.g. java -jar zk-backup.jar -import /zk/zkBackup_20200117114259
~~~~