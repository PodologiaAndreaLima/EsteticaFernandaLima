import api from "./api";
import API_CONFIG from "../config/apiConfig";

export const AppointmentService = {
  // Buscar todos os agendamentos (acesso admin)
  getAllAppointments: async () => {
    try {
      const response = await api.get(API_CONFIG.ENDPOINTS.APPOINTMENTS.BASE);
      return {
        success: true,
        appointments: response.data,
      };
    } catch (error) {
      console.error("Erro ao buscar agendamentos:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao buscar agendamentos",
      };
    }
  },

  // Buscar agendamentos do usuário logado
  getMyAppointments: async () => {
    try {
      const response = await api.get(
        API_CONFIG.ENDPOINTS.APPOINTMENTS.MY_APPOINTMENTS
      );
      return {
        success: true,
        appointments: response.data,
      };
    } catch (error) {
      console.error("Erro ao buscar seus agendamentos:", error);
      return {
        success: false,
        error:
          error.response?.data?.message || "Erro ao buscar seus agendamentos",
      };
    }
  },

  // Buscar agendamento por ID
  getAppointmentById: async (appointmentId) => {
    try {
      const response = await api.get(
        `${API_CONFIG.ENDPOINTS.APPOINTMENTS.BASE}/${appointmentId}`
      );
      return {
        success: true,
        appointment: response.data,
      };
    } catch (error) {
      console.error("Erro ao buscar agendamento:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao buscar agendamento",
      };
    }
  },

  // Criar novo agendamento
  createAppointment: async (appointmentData) => {
    try {
      const response = await api.post(
        API_CONFIG.ENDPOINTS.APPOINTMENTS.BASE,
        appointmentData
      );
      return {
        success: true,
        appointment: response.data,
      };
    } catch (error) {
      console.error("Erro ao criar agendamento:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao criar agendamento",
      };
    }
  },

  // Atualizar agendamento existente
  updateAppointment: async (appointmentId, appointmentData) => {
    try {
      const response = await api.put(
        `${API_CONFIG.ENDPOINTS.APPOINTMENTS.BASE}/${appointmentId}`,
        appointmentData
      );
      return {
        success: true,
        appointment: response.data,
      };
    } catch (error) {
      console.error("Erro ao atualizar agendamento:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao atualizar agendamento",
      };
    }
  },

  // Cancelar agendamento
  cancelAppointment: async (appointmentId, cancelReason) => {
    try {
      const response = await api.put(
        `${API_CONFIG.ENDPOINTS.APPOINTMENTS.BASE}/${appointmentId}/cancel`,
        { cancelReason }
      );
      return {
        success: true,
        appointment: response.data,
      };
    } catch (error) {
      console.error("Erro ao cancelar agendamento:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao cancelar agendamento",
      };
    }
  },

  // Confirmar agendamento (apenas admin/profissional)
  confirmAppointment: async (appointmentId) => {
    try {
      const response = await api.put(
        `${API_CONFIG.ENDPOINTS.APPOINTMENTS.BASE}/${appointmentId}/confirm`
      );
      return {
        success: true,
        appointment: response.data,
      };
    } catch (error) {
      console.error("Erro ao confirmar agendamento:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao confirmar agendamento",
      };
    }
  },

  // Buscar horários disponíveis para agendamento
  getAvailableTimes: async (date) => {
    try {
      const response = await api.get(
        `${API_CONFIG.ENDPOINTS.APPOINTMENTS.AVAILABLE_TIMES}?date=${date}`
      );
      return {
        success: true,
        availableTimes: response.data,
      };
    } catch (error) {
      console.error("Erro ao buscar horários disponíveis:", error);
      return {
        success: false,
        error:
          error.response?.data?.message ||
          "Erro ao buscar horários disponíveis",
      };
    }
  },
};
