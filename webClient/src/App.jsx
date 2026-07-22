import { useState, useEffect } from "react";
import "./App.css";

const API_BASE_URL = "http://localhost:8081";

function App() {
  const [cameras, setCameras] = useState([]);
  const [lastUpdate, setLastUpdate] = useState(new Date());

  const fetchCameras = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/cameras`);
      if (response.ok) {
        const data = await response.json();
        setCameras(data);
        setLastUpdate(new Date());
      }
    } catch (error) {
      console.error("Error obteniendo cámaras:", error);
    }
  };

  useEffect(() => {
    fetchCameras();
    const interval = setInterval(fetchCameras, 500);
    return () => clearInterval(interval);
  }, []);

  const handleStart = async (id) => {
    try {
      await fetch(`${API_BASE_URL}/api/start?id=${id}`);
      fetchCameras();
    } catch (error) {
      console.error("Error iniciando cámara:", error);
    }
  };

  const handleStop = async (id) => {
    try {
      await fetch(`${API_BASE_URL}/api/stop?id=${id}`);
      fetchCameras();
    } catch (error) {
      console.error("Error deteniendo cámara:", error);
    }
  };

  const activeStreams = cameras.filter((c) => c.isStreaming);
  const offlineNodes = cameras.filter((c) => !c.isStreaming).length;

  return (
    <div
      className="dashboard-container"
      style={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}
    >
      {/* Top Bar / Header Unificado */}
      <header
        className="top-bar"
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "16px 24px",
          borderBottom: "1px solid var(--border-color)",
          backgroundColor: "var(--bg-secondary, #1e1e1e)",
        }}
      >
        {/* Branding (Movido del sidebar) */}
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <div
            className="logo-glow"
            style={{
              width: "16px",
              height: "16px",
              borderRadius: "50%",
              backgroundColor: "#00bfff",
              boxShadow: "0 0 10px #00bfff",
            }}
          ></div>
          <h1 style={{ margin: 0, fontSize: "1.5rem", fontWeight: "bold" }}>
            Vision Central
          </h1>
        </div>

        {/* User / Status */}
        <div
          className="user-profile"
          style={{ display: "flex", alignItems: "center", gap: "20px" }}
        >
          <div
            className="status-indicator"
            style={{
              display: "flex",
              alignItems: "center",
              gap: "8px",
              fontSize: "0.9rem",
            }}
          >
            <span
              className="dot connected"
              style={{
                width: "8px",
                height: "8px",
                borderRadius: "50%",
                backgroundColor: "#00e676",
              }}
            ></span>
            Servidor Activo
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="main-content" style={{ padding: "24px", flex: 1 }}>
        {/* Dashboard Grid */}
        <div className="dashboard-grid">
          {/* Stat Cards */}
          <div className="card stat-card">
            <div className="stat-header">
              <span>Total Cámaras</span>
              <div className="stat-icon blue">📷</div>
            </div>
            <div className="stat-value">{cameras.length}</div>
            <div className="stat-trend up">
              <span className="trend-up">Activas en red</span>
            </div>
          </div>

          <div className="card stat-card">
            <div className="stat-header">
              <span>Transmisiones</span>
              <div className="stat-icon purple">▶️</div>
            </div>
            <div className="stat-value">{activeStreams.length}</div>
            <div className="stat-trend">
              <span>Cámaras enviando video</span>
            </div>
          </div>

          <div className="card stat-card">
            <div className="stat-header">
              <span>Nodos Inactivos</span>
              <div className="stat-icon orange">⏸️</div>
            </div>
            <div className="stat-value">{offlineNodes}</div>
            <div className="stat-trend">
              <span>Cámaras en espera</span>
            </div>
          </div>

          <div className="card stat-card">
            <div className="stat-header">
              <span>Última act.</span>
              <div className="stat-icon green">⏱️</div>
            </div>
            <div
              className="stat-value"
              style={{ fontSize: "1.5rem", marginTop: "10px" }}
            >
              {lastUpdate.toLocaleTimeString()}
            </div>
            <div className="stat-trend">
              <span className="trend-up">En tiempo real</span>
            </div>
          </div>

          {/* Main View Area (Video Streams) */}
          <div
            className="card main-chart-area"
            style={{ height: "auto", minHeight: "400px" }}
          >
            <div className="chart-header">
              <h3 className="chart-title">Transmisiones en Vivo</h3>
            </div>

            <div
              className="chart-placeholder"
              style={
                activeStreams.length > 0
                  ? {
                      background: "transparent",
                      border: "none",
                      display: "grid",
                      gridTemplateColumns:
                        "repeat(auto-fit, minmax(300px, 1fr))",
                      gap: "16px",
                    }
                  : {}
              }
            >
              {activeStreams.length === 0 ? (
                <div style={{ textAlign: "center", padding: "40px 0" }}>
                  <p style={{ fontSize: "3rem", margin: "0 0 16px 0" }}>📡</p>
                  <p>No hay transmisiones activas.</p>
                  <p
                    style={{
                      fontSize: "0.85rem",
                      marginTop: "8px",
                      opacity: 0.7,
                    }}
                  >
                    El sistema está en Modo Análisis o en espera.
                  </p>
                </div>
              ) : (
                activeStreams.map((cam) => (
                  <div
                    key={cam.id}
                    style={{
                      position: "relative",
                      borderRadius: "12px",
                      overflow: "hidden",
                      border: "1px solid var(--border-color)",
                      aspectRatio: "16/9",
                      backgroundColor: "#000",
                    }}
                  >
                    <div
                      style={{
                        position: "absolute",
                        top: 8,
                        left: 8,
                        background: "rgba(0,0,0,0.6)",
                        padding: "4px 8px",
                        borderRadius: "4px",
                        fontSize: "0.75rem",
                        zIndex: 10,
                        display: "flex",
                        alignItems: "center",
                        gap: "6px",
                        color: "#fff",
                      }}
                    >
                      <span
                        className="dot connected"
                        style={{
                          width: 6,
                          height: 6,
                          backgroundColor: "#ff4d4f",
                          borderRadius: "50%",
                        }}
                      ></span>
                      {cam.id.substring(0, 8)}
                    </div>
                    <img
                      src={`${API_BASE_URL}/stream?id=${cam.id}`}
                      alt={`Stream ${cam.id}`}
                      style={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                      }}
                      onError={(e) => {
                        e.target.style.display = "none";
                        e.target.parentElement.innerHTML +=
                          '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:#ff4d4f">Error de conexión</div>';
                      }}
                    />
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Active Nodes List (Control Panel) */}
          <div
            className="card active-nodes-area"
            style={{
              height: "auto",
              minHeight: "400px",
              maxHeight: "600px",
              overflowY: "auto",
            }}
          >
            <div className="chart-header">
              <h3 className="chart-title">Control de Nodos</h3>
            </div>

            <div className="node-list">
              {cameras.length === 0 ? (
                <p
                  style={{
                    color: "var(--text-secondary)",
                    textAlign: "center",
                    marginTop: "40px",
                  }}
                >
                  Buscando nodos en la red...
                </p>
              ) : (
                cameras.map((cam) => (
                  <div
                    className="node-item"
                    key={cam.id}
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      padding: "12px 0",
                      borderBottom: "1px solid var(--border-color)",
                    }}
                  >
                    <div
                      className="node-info"
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
                    >
                      <div className="node-icon" style={{ fontSize: "1.5rem" }}>
                        📷
                      </div>
                      <div className="node-details">
                        <h4 style={{ margin: 0, fontSize: "1rem" }}>
                          {cam.id.substring(0, 8)}
                        </h4>
                        <p
                          style={{
                            margin: 0,
                            fontSize: "0.8rem",
                            opacity: 0.7,
                          }}
                        >
                          {cam.ip}
                        </p>
                      </div>
                    </div>

                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
                    >
                      <span
                        className={`node-status`}
                        style={{
                          fontSize: "0.75rem",
                          fontWeight: "bold",
                          padding: "4px 8px",
                          borderRadius: "4px",
                          backgroundColor: cam.isStreaming
                            ? "rgba(0, 230, 118, 0.1)"
                            : "rgba(255, 165, 0, 0.1)",
                          color: cam.isStreaming ? "#00e676" : "orange",
                        }}
                      >
                        {cam.isStreaming ? "TRANSMITIENDO" : "ANALIZANDO"}
                      </span>

                      {cam.isStreaming ? (
                        <button
                          onClick={() => handleStop(cam.id)}
                          style={{
                            padding: "6px 12px",
                            background: "rgba(255, 77, 79, 0.1)",
                            color: "#ff4d4f",
                            border: "1px solid rgba(255,77,79,0.3)",
                            borderRadius: "6px",
                            fontSize: "0.8rem",
                            fontWeight: 600,
                            cursor: "pointer",
                          }}
                        >
                          DETENER
                        </button>
                      ) : (
                        <button
                          onClick={() => handleStart(cam.id)}
                          style={{
                            padding: "6px 12px",
                            background: "rgba(0, 191, 255, 0.1)",
                            color: "#00bfff",
                            border: "1px solid rgba(0,191,255,0.3)",
                            borderRadius: "6px",
                            fontSize: "0.8rem",
                            fontWeight: 600,
                            cursor: "pointer",
                          }}
                        >
                          FORZAR VIDEO
                        </button>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
