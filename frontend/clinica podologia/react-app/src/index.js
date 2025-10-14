import React from "react";
import ReactDOM from "react-dom/client";
import "./styles/global.css";
import "./styles/fix-visibility.css"; // Importando CSS para corrigir problemas de visibilidade
import App from "./App.jsx";
import reportWebVitals from "./reportWebVitals";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

reportWebVitals();
