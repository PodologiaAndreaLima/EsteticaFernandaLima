import toast from "react-hot-toast";

export const success = (msg) => toast.success(msg);
export const error = (msg) => toast.error(msg);
export const promise = (promise, msgs) => toast.promise(promise, msgs);

export default {
  success,
  error,
  promise,
};
