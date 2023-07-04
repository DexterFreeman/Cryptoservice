import React from "react";

const DetailsGrid = ({ market_data }) => {
  return (
    <div className="show-details">
      <h2>Details</h2>
      <div className="show-details-row">
        <div>
          <h4>Market Cap Rank</h4>
          <span>{market_data.market_cap_rank}</span>
        </div>
        <div>
          <h4>24H High</h4>
          <span>${market_data.high_24h.usd}</span>
        </div>
      </div>
      <div className="show-details-row">
        <div>
          <h4>24H Low</h4>
          <span>${market_data.low_24h.usd}</span>
        </div>
        <div>
          <h4>Circulating Supply</h4>
          <span>${market_data.circulating_supply}</span>
        </div>
      </div>
      <div className="show-details-row">
        <div>
          <h4>Current Price</h4>
          <span>${market_data.current_price.usd}</span>
        </div>

        <div>
          <h4>1 Year Change</h4>
          <span>{market_data.price_change_percentage_1y}%</span>
        </div>
      </div>
    </div>
  );
};

export default DetailsGrid;
