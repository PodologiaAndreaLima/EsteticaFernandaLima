import React from "react";
import Header from "../components/Header";
import Banner from "../components/Banner";
import QuemSomos from "../components/QuemSomos";
import Servicos from "../components/Servicos";
import Beneficios from "../components/Beneficios";
import SobreNos from "../components/SobreNos";
import EventosCarrossel from "../components/EventosCarrossel";
import Certificacoes from "../components/Certificacoes";
import Relatos from "../components/Relatos";
import Contato from "../components/Contato";
import Footer from "../components/Footer";

// Versão sem animações do componente Home
const HomeNoAnimation = () => {
  return (
    <div className="home">
      <Header />
      <Banner />
      <QuemSomos />
      <Servicos />
      <Beneficios />
      <SobreNos />
      <EventosCarrossel />
      <div className="section-divider"></div>
      <Certificacoes />
      <Relatos />
      <Contato />
      <Footer />
    </div>
  );
};

export default HomeNoAnimation;
