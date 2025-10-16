import React, { useEffect } from "react";
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

// Importar ScrollReveal para efeitos de animação
import ScrollReveal from "scrollreveal";

const Home = () => {
  useEffect(() => {
    // Garantir que o body não tenha a classe do sistema quando estiver na home
    document.body.classList.remove("sistema-body");

    // Configurações de ScrollReveal (ajustado para garantir que os elementos sejam exibidos)
    const sr = ScrollReveal({
      reset: false, // Alterado para false para evitar que os elementos desapareçam
      distance: "50px", // Distância reduzida para ser menos extrema
      duration: 800,
      delay: 200,
      opacity: 1, // Garante que os elementos sejam visíveis após a animação
    });

    // Aplicar efeitos aos elementos (ajustados para garantir visibilidade)
    sr.reveal(".card", { origin: "right", distance: "30px", opacity: 1 });
    sr.reveal(".relato", { origin: "right", distance: "30px", opacity: 1 });
    sr.reveal(".relatos", {
      origin: "bottom",
      distance: "30px",
      duration: 800,
      delay: 200,
      opacity: 0.8, // Valor ajustado para garantir visibilidade
    });

    // Efeito para o banner com vídeo foi removido pois não há mais conteúdo no banner

    // Efeitos simplificados para os carrosseis
    sr.reveal(".eventos-container", {
      origin: "bottom",
      distance: "20px",
      duration: 600,
      delay: 100,
      opacity: 0.8, // Aumentado para garantir visibilidade
      easing: "ease-in-out",
    });

    sr.reveal(".certificacoes-container", {
      origin: "bottom",
      distance: "20px",
      duration: 600,
      delay: 100,
      opacity: 0.8, // Aumentado para garantir visibilidade
      easing: "ease-in-out",
    });

    // Efeito para os títulos e ícones
    sr.reveal(".eventos-heading-wrapper, .certificacoes-heading-wrapper", {
      origin: "top",
      distance: "10px",
      duration: 500,
      delay: 100,
      opacity: 0.9, // Aumentado para garantir visibilidade
    });

    // Efeito para os cards (simplificado para garantir visibilidade)
    sr.reveal(".evento-card, .certificacao-card", {
      origin: "right",
      distance: "20px",
      duration: 600,
      interval: 100,
      opacity: 0.9, // Valor aumentado para garantir visibilidade
    }); // Cleanup function para desativar ScrollReveal ao desmontar o componente
    return () => {
      sr.destroy();
    };
  }, []);

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

export default Home;
