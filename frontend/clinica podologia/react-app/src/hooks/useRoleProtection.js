import { useAuth } from "../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

export const useRoleProtection = (requiredRoles = []) => {
  const { user, userRole, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (loading) return;

    if (!user || !userRole) {
      navigate("/login", { replace: true });
      return;
    }

    if (requiredRoles.length > 0 && !requiredRoles.includes(userRole)) {
      console.warn(`Acesso negado. Role necessária: ${requiredRoles.join(", ")}, Role do usuário: ${userRole}`);
      navigate("/sistema", { replace: true });
    }
  }, [user, userRole, requiredRoles, navigate, loading]);

  return userRole;
};