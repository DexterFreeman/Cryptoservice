import React from "react";

const Button = ({ handleClick, innerText }) => {
  return (
    <div class="container" onClick={handleClick}>
      <a href="#" class="button">
        <div class="button__line"></div>
        <div class="button__line"></div>
        <span class="button__text">{innerText}</span>
        <div class="button__drow1"></div>
        <div class="button__drow2"></div>
      </a>
    </div>
  );
};

export default Button;
