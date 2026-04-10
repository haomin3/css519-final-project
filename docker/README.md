## Docker commands
Confirmed working on macOS

1. Build or rebuild the JAR  
   `mvn clean package`

2. Build or rebuild the Docker image  
   `docker build -t css519-mock-product .`

3. Run the Docker image  
   `docker run -p 8080:8080 css519-mock-product`
