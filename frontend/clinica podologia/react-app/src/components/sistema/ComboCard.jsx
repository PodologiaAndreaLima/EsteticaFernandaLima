import React from "react";
import "./ComboCard.css";

const ComboCard = ({ combo, onVisualizar, onEditar, onExcluir }) => {
    return (
    <div className="card-combo">
      <div className="card-header">
        <h3>{combo.nome}</h3>
      </div>

      <div className="card-content">
        <p className="card-descricao">
          <span>Descrição:</span> {combo.descricao}
        </p>

        <div className="card-valores">
          <div className="valor-item">
            <span>Valor final:</span>
            <strong>R$ {combo.valorFinal}</strong>
          </div>
        </div>
      </div>

      <div className="card-actions">
        <button
          className="card-button-visualizar"
          onClick={() => onVisualizar(combo)}
        >
          Visualizar
        </button>
        <button
          className="card-button-editar"
          onClick={() => onEditar(combo)}
        >
          Editar
        </button>
        <button
          className="card-button-excluir"
          onClick={() => onExcluir(combo.id)}
        >
          Excluir
        </button>
      </div>
    </div>
  );
}

export default ComboCard;