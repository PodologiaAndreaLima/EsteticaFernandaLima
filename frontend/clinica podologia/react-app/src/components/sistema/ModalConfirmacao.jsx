import React from "react";
import "./ModalConfirmacao.css";

/**
 * Componente de Modal de Confirmação para ações críticas como exclusão
 * @param {Object} props - Propriedades do componente
 * @param {boolean} props.estaAberto - Define se o modal está visível
 * @param {Function} props.aoFechar - Função chamada ao fechar o modal (cancelar)
 * @param {Function} props.aoConfirmar - Função chamada ao confirmar a ação
 * @param {string} props.titulo - Título do modal
 * @param {string} props.mensagem - Mensagem de confirmação
 * @param {string} props.textoBotaoConfirmar - Texto do botão de confirmação
 * @param {string} props.textoBotaoCancelar - Texto do botão de cancelar
 * @param {string} props.tipo - Tipo do modal (exclusao, edicao)
 */
const ModalConfirmacao = ({
  estaAberto,
  aoFechar,
  aoConfirmar,
  titulo = "Tem certeza?",
  mensagem = "Os dados serão excluídos permanentemente, não sendo possível recuperá-los",
  textoBotaoConfirmar = "Excluir",
  textoBotaoCancelar = "Descartar",
  tipo = "exclusao",
}) => {
  if (!estaAberto) return null;

  // Determinar classes CSS com base no tipo
  const classeModal = `modal-confirmacao modal-${tipo}`;
  const classeBotaoConfirmar = `botao-confirmar botao-${tipo}`;

  return (
    <div className="overlay-confirmacao">
      <div className={classeModal}>
        <div className="icone-modal">
          {tipo === "exclusao" && <span>❌</span>}
          {tipo === "edicao" && <span>✏️</span>}
        </div>

        <h2>{titulo}</h2>
        <p>{mensagem}</p>

        <div className="acoes-confirmacao">
          <button className="botao-cancelar" onClick={aoFechar}>
            {textoBotaoCancelar}
          </button>
          <button className={classeBotaoConfirmar} onClick={aoConfirmar}>
            {textoBotaoConfirmar}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalConfirmacao;
