import React, { useState, useEffect, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faGraduationCap,
  faChevronLeft,
  faChevronRight,
  faCertificate,
} from "@fortawesome/free-solid-svg-icons";
import "./Certificacoes.css";
import "../../styles/fix-scrollbars.css";

// Dados de certificações - substitua pelo conteúdo real quando disponível
const certificacoesData = [
  {
    id: 1,
    titulo: "Especialização em Estética Avançada",
    instituicao: "Universidade de São Paulo",
    ano: "2022",
    imagem:
      "https://via.placeholder.com/400x300?text=Certificado+Estetica+Avancada",
  },
  {
    id: 2,
    titulo: "Técnicas Avançadas em Tratamentos Faciais",
    instituicao: "Instituto Brasileiro de Estética",
    ano: "2021",
    imagem:
      "https://via.placeholder.com/400x300?text=Certificado+Tecnicas+Faciais",
  },
  {
    id: 3,
    titulo: "Pós-Graduação em Cosmetologia",
    instituicao: "Faculdade de Ciências da Saúde",
    ano: "2020",
    imagem: "https://via.placeholder.com/400x300?text=Certificado+Cosmetologia",
  },
  {
    id: 4,
    titulo: "Especialização em Massoterapia",
    instituicao: "Instituto de Terapias Integrativas",
    ano: "2019",
    imagem: "https://via.placeholder.com/400x300?text=Certificado+Massoterapia",
  },
  {
    id: 5,
    titulo: "Curso Avançado em Estética Corporal",
    instituicao: "Centro de Estudos em Estética",
    ano: "2018",
    imagem:
      "https://via.placeholder.com/400x300?text=Certificado+Estetica+Corporal",
  },
];

const Certificacoes = () => {
  // Seção de certificações comentada a pedido — não renderizar no site.
  // Para reativar a seção, substitua `return null` pelo JSX original.
  return null;
};

export default Certificacoes;
