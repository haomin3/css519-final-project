## Docker commands
Confirmed working on macOS

1. Build or rebuild the Docker image  
`docker build -t css519-oe-dashboard .`

2. Run the Docker image  
`docker run -p 8081:80 css519-oe-dashboard`

## Note
The OE dashboard now reads live metrics from the product endpoint at `http://localhost:8080/oe`

Run the product container first: `docker run -p 8080:8080 css519-mock-product`

Then run the dashboard container: `docker run -p 8081:80 css519-oe-dashboard`

Open the dashboard at `http://localhost:8081`.
