// Exportando todos os serviços da aplicação a partir de um único ponto
import api from "./api";
import { AuthService } from "./authService";
import { UserService } from "./userService";
import { AppointmentService } from "./appointmentService";

// Exportação padrão como objeto para facilitar o uso
export { api, AuthService, UserService, AppointmentService };
