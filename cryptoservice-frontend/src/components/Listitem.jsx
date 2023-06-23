import React from "react";
import { Link } from "react-router-dom";
const Listitem = ({ coin }) => {
  return (
    <div className="home-crypto">
      <Link to={`/${coin.id}`}>
        <span className="home-crypto-image">
          <img src={coin.image} alt="" srcset="" />
        </span>
        <span className="home-crypto-name">{coin.name}</span>
        {coin.priceBtc && <span className="home-crypto-prices">
          <span className="home-crypto-btc">
            <img src={"/bitcoin.webp"} />
            BTC:{coin.priceBtc}
          </span>
          <span className="home-crypto-usd">${coin.priceUsd}</span>
        </span>}
      </Link>
    </div>
  );
};

export default Listitem;
