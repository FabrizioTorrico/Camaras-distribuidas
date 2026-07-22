#!/bin/bash
# Startup script for EdgeNode
# Usage: ./start_node.sh [SERVER_IP] [SERVER_PORT] [CAM_ID]

# Ensure we are in the script's directory
cd "$(dirname "$0")"

# Default Configuration
# Change 'localhost' to the IP of the CentralServer
SERVER_IP=${1:-"localhost"} 
SERVER_PORT=${2:-5555}
CAM_ID=${3:-"cam1"}

echo "========================================="
echo " Starting EdgeNode..."
echo " Target Server: $SERVER_IP:$SERVER_PORT"
echo " Camera ID:     $CAM_ID"
echo "========================================="

# Run the application
# Ensure the JAR is built. If not, you might need 'mvn package' first.
java -jar target/edgeNode-1.0-SNAPSHOT.jar "$SERVER_IP" "$SERVER_PORT" "$CAM_ID"
