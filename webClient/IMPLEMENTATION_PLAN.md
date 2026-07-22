# Implementation Plan: Web Client Dashboard for Distributed Vision System

## 1. Overview

The Web Client is a React/Vite application designed to provide a "premium" dashboard interface to control and view camera streams managed by the Central Server. It will communicate with the `WebServer` components of the Central Server running on port `8081`.

## 2. API Integration Analysis

Based on the analysis of `CentralServer.java` and `WebServer.java`, the following endpoints will be consumed:

- **Base URL:** `http://localhost:8081`
- **Endpoints:**
  1. `GET /api/cameras`: Returns a JSON array of registered cameras `[{ "id": "uuid", "ip": "127.0.0.1", "isStreaming": boolean }]`.
  2. `GET /api/start?id={id}`: Starts the streaming for a specific camera node.
  3. `GET /api/stop?id={id}`: Stops the streaming for a specific camera node.
  4. `GET /stream?id={id}`: Provides an MJPEG video stream (multipart/x-mixed-replace) to be used directly in an `<img />` tag `src`.

## 3. UI/UX Design

The application will use a dark-mode premium aesthetic (already set up in `index.css` and `App.css`).
The Dashboard will be divided into:

- **Sidebar**: Branding, navigation links.
- **Top Bar**: Search input, User profile, Global status indicator.
- **Dashboard Grid (Main Content)**:
  - **Stat Cards**: Total Cameras, Active Streams, Offline Nodes, Server Uptime.
  - **Main Chart Area**: Live view of the camera streams (will display a grid of `<img src="http://localhost:8081/stream?id=..." />` for active streams).
  - **Active Nodes Area**: List of cameras with their status, IP, and control buttons (PLAY / STOP) to toggle stream state via `/api/start` and `/api/stop`.

## 4. Implementation Steps

1. **Initialize API Service**: Create an API utility to handle fetches to `localhost:8081`.
2. **Build Dashboard Layout**: Implement the base layout structure using standard React components in `App.jsx`.
3. **Implement Camera Polling**: Use `useEffect` and `setInterval` to periodically poll `/api/cameras` every 2 seconds to keep the camera list up to date.
4. **Implement Camera Controls**: Add event handlers (`handleStart`, `handleStop`) bound to the respective API endpoints.
5. **Implement Stream Viewer**: Create a CameraView component that mounts an `<img>` tag and handles stream mounting/unmounting gracefully.
6. **Refine Styling**: Apply the existing CSS classes from `App.css` to build out the premium visual design.

## 5. Review & Testing

Once implemented, start the React server (`npm run dev`) and test HTTP requests connectivity to the Central Server. Ensure there are no CORS errors (though `WebServer.java` manually includes broad CORS headers) and that the MJPEG streams load correctly in the browser.
