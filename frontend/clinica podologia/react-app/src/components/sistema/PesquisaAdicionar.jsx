import React from "react";
import "./PesquisaAdicionar.css";

/**
 * PesquisaAdicionar
 * Props:
 * - value: string
 * - onChange: function(event)
 * - onAdd: function()
 * - addLabel: string (button label)
 * - placeholder: string (optional)
 */
const PesquisaAdicionar = ({
  value,
  onChange,
  onAdd,
  addLabel = "Adicionar",
  placeholder = "Pesquisar...",
}) => {
  return (
    <div className="container-pesquisa">
      <input
        type="text"
        placeholder={placeholder}
        className="campo-pesquisa"
        value={value}
        onChange={onChange}
      />
      <button className="botao-adicionar" onClick={onAdd}>
        {addLabel}
      </button>
    </div>
  );
};

export default PesquisaAdicionar;
