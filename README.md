# search

Prerequsite: Please have docker installed on the system

Install Elasticsearch
Step 1: execute the command: docker pull elasticsearch:7.2.0
Step 2:  docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.2.0


Index data:
Note: Elasticsearch must be running for indexing.
Step 1: Create a folder and copy the files under indexer/src/main/resources/files/
Step 2: Build the project using mvn clean install
Step 3: CD into the target directory
Step 4: Run java -jar indexer.jar --data.directory=<<path to the folder created in step 1>>


Search:
Step 1: navigate to search-engin directory
Step 2: Build the project using mvn clean install
Step 3: CD into the target directory
Step 4: Run java -jar search-engine.jar --data.directory=<path to the folder created in Index Data step 1>>

To Search:
Step 1: Open your favorite browser
Step 2: Enter the URL: http://localhost:8080/swagger-ui.html
Step 3: Expand Search Controller
Step 4: Expand the service by clicking on /service
Step 5: Click "Try it out"
Step 6: Enter the search method  [1 - 3] and search term
Step 7: Click "Execute"
