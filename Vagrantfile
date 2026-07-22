Vagrant.configure("2") do |config|
  # Base box de Ubuntu
  config.vm.box = "ubuntu/jammy64"

  # =========================================================
  # 1. CENTRAL SERVER
  # =========================================================
  config.vm.define "central_server" do |server|
    server.vm.hostname = "centralserver"
    server.vm.network "private_network", ip: "192.168.56.10"
    
    # Exponer puerto 8081 para que webClient pueda conectarse
    server.vm.network "forwarded_port", guest: 8081, host: 8081, auto_correct: true

    server.vm.provider "virtualbox" do |vb|
      vb.memory = "1024"
      vb.cpus = 1
    end

    server.vm.provision "shell", inline: <<-SHELL
      echo "=== Aprovisionando Central Server ==="
      apt-get update
      apt-get install -y openjdk-21-jdk maven

      echo "=== Compilando Central Server ==="
      cd /vagrant/centralServer
      mvn clean compile

      echo "=== Creando script de inicio ==="
      cat << 'EOF' > /home/vagrant/start_central.sh
#!/bin/bash
cd /vagrant/centralServer
mvn exec:java -Dexec.mainClass="org.alumnosinfo.tpdistribuido.CentralServer"
EOF
      chmod +x /home/vagrant/start_central.sh
      echo "✅ Central Server listo. Para iniciar: ./start_central.sh"
    SHELL
  end

  # =========================================================
  # 2. EDGE NODE 1
  # =========================================================
  config.vm.define "edge_node_1" do |node1|
    node1.vm.hostname = "edgenode1"
    node1.vm.network "private_network", ip: "192.168.56.20"

    node1.vm.provider "virtualbox" do |vb|
      vb.memory = "1024"
      vb.cpus = 1
      # Soporte general de USB (EHCI/XHCI)
      vb.customize ["modifyvm", :id, "--usb", "on"]
      vb.customize ["modifyvm", :id, "--usbehci", "on"]
      vb.customize ["modifyvm", :id, "--usbxhci", "on"]
    end

    node1.vm.provision "shell", inline: <<-SHELL
      echo "=== Aprovisionando Edge Node 1 ==="
      apt-get update
      apt-get install -y openjdk-21-jdk maven v4l-utils

      echo "=== Compilando Edge Node ==="
      cd /vagrant/edgeNode
      mvn clean compile

      echo "=== Creando script de inicio ==="
      cat << 'EOF' > /home/vagrant/start_edge.sh
#!/bin/bash
cd /vagrant/edgeNode
SRC=${1:-"0"}
# Permite usar cualquier índice de cámara (0, 1...), stream URL o archivo de video
mvn exec:java -Dexec.mainClass="org.alumnosinfo.tpdistribuido.EdgeNode" -Dexec.args="192.168.56.10 CAM_01 $SRC"
EOF
      chmod +x /home/vagrant/start_edge.sh
      echo "✅ Edge Node 1 listo. Para iniciar: ./start_edge.sh [0|1|http://...|/path/to/video.mp4]"
    SHELL
  end

  # =========================================================
  # 3. EDGE NODE 2
  # =========================================================
  config.vm.define "edge_node_2" do |node2|
    node2.vm.hostname = "edgenode2"
    node2.vm.network "private_network", ip: "192.168.56.21"

    node2.vm.provider "virtualbox" do |vb|
      vb.memory = "1024"
      vb.cpus = 1
      # Soporte general de USB (EHCI/XHCI)
      vb.customize ["modifyvm", :id, "--usb", "on"]
      vb.customize ["modifyvm", :id, "--usbehci", "on"]
      vb.customize ["modifyvm", :id, "--usbxhci", "on"]
    end

    node2.vm.provision "shell", inline: <<-SHELL
      echo "=== Aprovisionando Edge Node 2 ==="
      apt-get update
      apt-get install -y openjdk-21-jdk maven v4l-utils

      echo "=== Compilando Edge Node ==="
      cd /vagrant/edgeNode
      mvn clean compile

      echo "=== Creando script de inicio ==="
      cat << 'EOF' > /home/vagrant/start_edge.sh
#!/bin/bash
cd /vagrant/edgeNode
SRC=${1:-"0"}
# Permite usar cualquier índice de cámara (0, 1...), stream URL o archivo de video
mvn exec:java -Dexec.mainClass="org.alumnosinfo.tpdistribuido.EdgeNode" -Dexec.args="192.168.56.10 CAM_02 $SRC"
EOF
      chmod +x /home/vagrant/start_edge.sh
      echo "✅ Edge Node 2 listo. Para iniciar: ./start_edge.sh [0|1|http://...|/path/to/video.mp4]"
    SHELL
  end
end
