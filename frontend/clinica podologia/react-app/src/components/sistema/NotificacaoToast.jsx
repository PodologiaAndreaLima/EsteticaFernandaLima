import React, { useEffect } from 'react';
import './NotificacaoToast.css';

/**
 * Componente de notificação rápida (toast)
 * @param {Object} props
 * @param {string} props.mensagem - Mensagem a ser exibida
 * @param {boolean} props.visivel - Define se a notificação está visível
 * @param {Function} props.aoFechar - Função chamada ao fechar a notificação
 * @param {number} props.tempo - Tempo em ms para ocultar automaticamente (padrão: 3000)
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

  return (
    <div className="notificacao-toast">
      {mensagem}
    </div>
  );
};

export default NotificacaoToast;
