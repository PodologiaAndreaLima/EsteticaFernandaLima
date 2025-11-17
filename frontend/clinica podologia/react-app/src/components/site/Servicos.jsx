import React from "react";
import "./Servicos.css";
import CarrosselServico from "./CarrosselServico";

const services = [
  {
    img: "/assets/micropigmentação-foto.jpg",
    title: "Micropigmentação de Sobrancelhas",
    desc: "Micropigmentação de sobrancelhas é uma técnica estética que deposita pigmentos na pele para preencher falhas, definir e realçar a sobrancelha, resultando em um visual mais denso e harmonioso. Ao contrário de tatuagens, ela é semipermanente, sendo aplicada em uma camada superficial da pele e exigindo retoques periódicos para manter o resultado.",
  },
  {
    img: "/assets/design-de-sombrancelhas-foto.jpg",
    title: "Design de Sobrancelhas",
    desc: "Design de sobrancelhas é um procedimento estético que modela e realça o formato natural das sobrancelhas, respeitando as proporções do rosto e as características individuais da pessoa.",
  },
  {
    img: "/assets/design de sobrancelhas com tintura-foto.jpg",
    title: "Design de Sobrancelhas com Tintura",
    desc: "Inclui o procedimento de design tradicional com aplicação de tintura para realçar ainda mais o formato e a cor das sobrancelhas. Também é indicado para tingimento de pelos brancos nas sobrancelhas.",
  },
  {
    img: "/assets/limpeza-de-pele-foto.jpg",
    title: "Limpeza de Pele",
    desc: "Limpeza de pele é um procedimento estético que remove profundamente impurezas, células mortas e excesso de oleosidade da pele, como cravos e espinhas.",
  },
  {
    img: "/assets/peeling-facial-v1.jpg",
    title: "Peeling Facial",
    desc: "Peeling facial: técnica para renovar a pele, estimular colágeno e melhorar textura e tonalidade.",
  },
  {
    img: "/assets/estrias.jpeg",
    title: "Tratamento de Estrias - Relive Skin Method",
    desc: "Relive Skin Method é um método multidisciplinar para o tratamento de estrias e cicatrizes, que combina micropigmentação com técnicas de estética avançada e cosmetologia.",
  },
  {
    img: "/assets/depilacao-foto.webp",
    title: "Depilação",
    desc: "Oferecemos depilação masculina e feminina com técnicas seguras e produtos de qualidade para garantir conforto e um resultado uniforme.",
  },
  {
    img: "/assets/lash-lifting-foto.jpg",
    title: "Lash Lifting",
    desc: "Lash lifting é um procedimento estético que curva e alonga os cílios naturais, criando um efeito com durabilidade prolongada.",
  },
  {
    img: "/assets/capilar-foto.jpg",
    title: "Serviço Capilar",
    desc: "Tratamentos capilares para saúde, beleza e fortalecimento dos fios.",
  },
];

const Servicos = () => {
  return (
    <section className="servicos" id="servicos">
      <div className="servicos-header">
        <h3>Nossos Serviços</h3>
        <h1>Cuidados Personalizados para Realçar Sua Beleza Natural</h1>
      </div>
      <CarrosselServico services={services} />
    </section>
  );
};

export default Servicos;
