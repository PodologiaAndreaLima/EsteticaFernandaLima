import { UserService } from "./userService";

// Wrapper para compatibilidade com o padrão usado em outros services
export default {
  async list() {
    try {
      const result = await UserService.getAllUsers();
      if (result.success) {
        return { success: true, data: result.users || [] };
      }
      return { success: false, data: [] };
    } catch (err) {
      console.error("usuarioService.list error", err);
      return { success: false, data: [] };
    }
  },

  async listAll() {
    return this.list();
  },

  async getById(id) {
    try {
      const result = await UserService.getUserById(id);
      if (result.success) {
        return { success: true, data: result.user };
      }
      return { success: false, error: result.error };
    } catch (err) {
      console.error("usuarioService.getById error", err);
      throw err;
    }
  },

  async create(data) {
    try {
      const result = await UserService.createUser(data);
      return result;
    } catch (err) {
      console.error("usuarioService.create error", err);
      throw err;
    }
  },

  async update(id, data) {
    try {
      const result = await UserService.updateUser(id, data);
      return result;
    } catch (err) {
      console.error("usuarioService.update error", err);
      throw err;
    }
  },

  async remove(id) {
    try {
      const result = await UserService.deleteUser(id);
      return result;
    } catch (err) {
      console.error("usuarioService.remove error", err);
      throw err;
    }
  },

  async delete(id) {
    return this.remove(id);
  }
};
