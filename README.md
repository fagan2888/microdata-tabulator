### Quick Instructions and Information

#### Compiling and Running

We used the Eclipse IDE for developing this project, and as such, we relied upon the feature "Export As Runnable Jar" (Extract Libraries into Generated Jar File) to create the final jar file.

#### Usage

In order to use this project with your own data, you will need know or have the following

1. Data in a fixed width file format (included is sample data in 'data/us2002a_10k_usa.dat.gz')
2. A CSV file that contains the location information of your variables within your data file.  Four columns, "record type", "variable name", "start position" and "variable width"
3. Rules.  You need a specially crafted YAML file that defines your intended tabulations.  See 'rules.yml' for examples.

##### Running

```
java -Xmx8000M -server -jar tabulator.jar rules.yml var_positions.csv
```


```
  Microdata Tabulator
  https://github.com/mnpopcenter/microdata-tabulator
  Copyright (c) 2012-2017 Regents of the University of Minnesota

  Contributors:
    Alex Jokela, Minnesota Population Center, University of Minnesota
    Pranjul Yadav, Minnesota Population Center, University of Minnesota
 
  This project is licensed under the Mozilla Public License, version 2.0 (the
  "License"). A copy of the License is in the project file "LICENSE.txt",
  and is also available at https://www.mozilla.org/en-US/MPL/2.0/.
 ```