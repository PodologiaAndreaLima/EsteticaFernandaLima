import React, { useEffect } from "react";
import "./NotificacaoToast.css";

/**
 * Componente de notificação rápida (toast)
 * @param {Object} props
 * @param {string} props.mensagem - Mensagem a ser exibida
 * @param {boolean} props.visivel - Define se a notificação está visível
 * @param {Function} props.aoFechar - Função chamada ao fechar a notificação
 * @param {number} props.tempo - Tempo em ms para ocultar automaticamente (padrão: 3000)
 * @param {string} [props.tipo] - Tipo da notificação: atualmente apenas 'sucesso'
 */
const NotificacaoToast = ({ mensagem, visivel, aoFechar, tempo = 3000 }) => {
  useEffect(() => {
    if (visivel) {
      const timer = setTimeout(() => {
        aoFechar();
      }, tempo);
      return () => clearTimeout(timer);
    }
  }, [visivel, tempo, aoFechar]);

  if (!visivel) return null;

  // Ícone de sucesso simples
  const icone = "✔️";
  return (
    <div className="notificacao-toast notificacao-toast--sucesso">
      <span className="notificacao-toast__icone">{icone}</span>
      <span>{mensagem}</span>
    </div>
  );
};

export default NotificacaoToast;
