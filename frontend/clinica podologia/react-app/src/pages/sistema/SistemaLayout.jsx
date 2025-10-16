import React, { useEffect } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../../components/sistema/Sidebar";
import "./SistemaLayout.css";

const SistemaLayout = () => {
  // Adicionando classe ao body para controlar o overflow apenas no sistema
  useEffect(() => {
    document.body.classList.add("sistema-body");

    return () => {
      document.body.classList.remove("sistema-body");
    };
  }, []);

  return (
    <div className="sistema-layout">
      <Sidebar />
      <main className="sistema-content">
        <div className="content-area">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default SistemaLayout;
