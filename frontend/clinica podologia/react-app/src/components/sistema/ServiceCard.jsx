import React from "react";
import "./ServiceCard.css";

const ServiceCard = ({ servico, onVisualizar, onEditar, onExcluir }) => {
  return (
    <div className="card-servico">
      <div className="card-header">
        <h3>{servico.nome}</h3>
      </div>
      <div className="card-content">
        <p className="card-descricao-truncada">{servico.descricao}</p>

        <div className="card-valores">
          <div className="valor-item">
            <span>Valor de Custo</span>
            <strong>R$ {servico.valorCusto}</strong>
          </div>
          <div className="valor-item">
            <span>Valor de Venda</span>
            <strong>R$ {servico.valorVenda}</strong>
          </div>
        </div>
      </div>
      <div className="card-actions">
        <button
          className="card-button-visualizar"
          onClick={() => onVisualizar(servico)}
        >
          Visualizar
        </button>
        <button
          className="card-button-editar"
          onClick={() => onEditar(servico)}
        >
          Editar
        </button>
        <button
          className="card-button-excluir"
          onClick={() => onExcluir(servico.id)}
        >
          Excluir
        </button>
      </div>
    </div>
  );
};

export default ServiceCard;
