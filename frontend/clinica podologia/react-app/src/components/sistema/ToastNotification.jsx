import React, { useEffect } from "react";
import "./ToastNotification.css";

const ToastNotification = ({ message, show, onClose, duration = 3000 }) => {
  useEffect(() => {
    if (show) {
      const timer = setTimeout(() => {
        onClose();
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [show, duration, onClose]);

  if (!show) return null;

  return <div className="toast-notification">{message}</div>;
};

export default ToastNotification;
