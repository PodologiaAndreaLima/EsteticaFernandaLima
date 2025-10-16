import React from "react";
import Header from "../../components/site/Header";
import Banner from "../../components/site/Banner";
import QuemSomos from "../../components/site/QuemSomos";
import Servicos from "../../components/site/Servicos";
import Beneficios from "../../components/site/Beneficios";
import SobreNos from "../../components/site/SobreNos";
import EventosCarrossel from "../../components/site/EventosCarrossel";
import Certificacoes from "../../components/site/Certificacoes";
import Relatos from "../../components/site/Relatos";
import Contato from "../../components/site/Contato";
import Footer from "../../components/site/Footer";

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
