import React from "react";
import "./ProductCard.css";

const ProductCard = ({ produto, onVisualizar, onEditar, onExcluir }) => {
  return (
    <div className="card-produto">
      <div className="card-header">
        <h3>{produto.nome}</h3>
        <span className="card-categoria">{produto.categoria}</span>
      </div>

      <div className="card-content">
        <p className="card-marca">
          <span>Marca:</span> {produto.marca}
        </p>

        <div className="card-valores">
          <div className="valor-item">
            <span>Compra:</span>
            <strong>R$ {produto.valorCompra}</strong>
          </div>
          <div className="valor-item">
            <span>Venda:</span>
            <strong>R$ {produto.valorVenda}</strong>
          </div>
        </div>

        <p className="card-descricao-truncada">{produto.descricao}</p>
      </div>

      <div className="card-actions">
        <button
          className="card-button-visualizar"
          onClick={() => onVisualizar(produto)}
        >
          Visualizar
        </button>
        <button
          className="card-button-editar"
          onClick={() => onEditar(produto)}
        >
          Editar
        </button>
        <button
          className="card-button-excluir"
          onClick={() => onExcluir(produto.id)}
        >
          Excluir
        </button>
      </div>
    </div>
  );
};

export default ProductCard;
