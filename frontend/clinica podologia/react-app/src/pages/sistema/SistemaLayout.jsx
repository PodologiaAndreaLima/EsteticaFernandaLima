import React, { useEffect } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../../components/sistema/Sidebar";
import "./SistemaLayout.css";
import { useAuth } from "../../contexts/AuthContext";

const SistemaLayout = () => {
  const { userRole } = useAuth();

  useEffect(() => {
    document.body.classList.add("sistema-body");
    return () => {
      document.body.classList.remove("sistema-body");
    };
  }, []);

  return (
    <div className="sistema-layout">
      <Sidebar userRole={userRole} /> {/* PASSAR userRole AQUI */}
      <main className="sistema-content">
        <div className="content-area">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default SistemaLayout;
