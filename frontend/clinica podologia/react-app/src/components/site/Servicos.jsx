import React from "react";
import "./Servicos.css";

const Servicos = () => {
  return (
    <section className="servicos" id="servicos">
      <div className="servicos-header">
        <h3>Nossos Serviços</h3>
        <h1>Cuidados Personalizados para Realçar Sua Beleza Natural</h1>
      </div>
      {/* Serviço - Micropigmentação de Sobrancelhas */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Micropigmentação de Sobrancelhas</h2>
          <p>
            Micropigmentação de sobrancelhas é uma técnica estética que deposita
            pigmentos na pele para preencher falhas, definir e realçar a
            sobrancelha, resultando em um visual mais denso e harmonioso. Ao
            contrário de tatuagens, ela é semipermanente, sendo aplicada em uma
            camada superficial da pele e exigindo retoques periódicos para
            manter o resultado.
          </p>
        </div>
      </div>
      {/* Serviço - Design de Sobrancelhas - Brow Lamination */}
      {/* Serviço - Design de Sobrancelhas */}
      <div className="servico texto-first">
        <div className="texto">
          <h2>Design de Sobrancelhas</h2>
          <p>
            Design de sobrancelhas é um procedimento estético que modela e
            realça o formato natural das sobrancelhas, respeitando as proporções
            do rosto e as características individuais da pessoa.
          </p>
        </div>
        <div className="circle">
          <img
            src="/assets/design-sobrancelhas-foto.jpg"
            alt="Design de Sobrancelhas"
            className="servico-img"
          />
        </div>
      </div>

      {/* Serviço - Design de Sobrancelhas com Tintura */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Design de Sobrancelhas com Tintura</h2>
          <p>
            Inclui o procedimento de design tradicional com aplicação de tintura
            para realçar ainda mais o formato e a cor das sobrancelhas.
          </p>
        </div>
      </div>

      {/* Serviço - Micropigmentação de Sobrancelhas */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Micropigmentação de Sobrancelhas</h2>
          <p>
            Micropigmentação de sobrancelhas é uma técnica estética que deposita
            pigmentos na pele para preencher falhas, definir e realçar a
            sobrancelha, resultando em um visual mais denso e harmonioso. Ao
            contrário de tatuagens, ela é semipermanente, sendo aplicada em uma
            camada superficial da pele e exigindo retoques periódicos para
            manter o resultado.
          </p>
        </div>
      </div>
      {/* Serviço 2 - Limpeza de Pele */}
      <div className="servico texto-first">
        <div className="texto">
          <h2>Limpeza de Pele</h2>
          <p>
            Limpeza de pele é um procedimento estético que remove profundamente
            impurezas, células mortas e excesso de oleosidade da pele, como
            cravos e espinhas.
          </p>
        </div>
        <div className="circle">
          <img
            src="/assets/limpeza-de-pele-foto.jpg"
            alt="Limpeza de Pele"
            className="servico-img"
          />
        </div>
      </div>
      {/* Serviço 3 - Peeling Facial */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Peeling Facial</h2>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur.
          </p>
        </div>
      </div>
      {/* Serviço 3 */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Tratamento de Estrias - Relive Skin Method</h2>
          <p>
            Relive Skin Method é um método multidisciplinar para o tratamento de
            estrias e cicatrizes, que combina esteticamente a micropigmentação
            com técnicas de estética avançada e cosmetologia. O objetivo é
            restaurar a aparência da pele e reduzir o diâmetro das estrias e
            melhorar a elasticidade, não sendo apenas uma camuflagem.
          </p>
        </div>
      </div>
      {/* Serviço 4 */}
      <div className="servico texto-first">
        <div className="texto">
          <h2>Depilação Completa</h2>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur.
          </p>
        </div>
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
      </div>
      {/* Serviço 5 - Lash Lifting */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Lash Lifting</h2>
          <p>
            Lash lifting é um procedimento estético que curva e alonga os cílios
            naturais, criando um efeito similar ao uso de curvex e máscara mas
            com durabilidade prolongada. A técnica não utiliza fios sintéticos,
            mas sim aplica produtos específicos para moldar os cílios.
          </p>
        </div>
      </div>
      {/* Serviço Capilar */}
      <div className="servico">
        <div className="circle">
          <img src="" alt="Serviço Capilar" className="servico-img" />
        </div>
        <div className="texto">
          <h2>Serviço Capilar</h2>
          <p>
            Tratamentos capilares para saúde, beleza e fortalecimento dos fios.
          </p>
        </div>
      </div>
    </section>
  );
};

export default Servicos;
