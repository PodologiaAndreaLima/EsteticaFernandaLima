import React from "react";
import "./Banner.css";

const Banner = () => {
  return (
    <section className="banner">
      <div className="video-background">
        <video autoPlay loop muted className="video-bg" playsInline>
          <source src="/assets/videoplayback.mp4" type="video/mp4" />
          Seu navegador não suporta vídeos HTML5.
        </video>
        <div className="video-overlay"></div>
      </div>
    </section>
  );
};

export default Banner;
