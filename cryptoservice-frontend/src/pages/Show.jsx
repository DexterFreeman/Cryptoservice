import React from "react";
import showStore from "../store/showStore";
import { useParams } from "react-router-dom";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

export const Show = () => {
  const store = showStore();
  const params = useParams();

  React.useEffect(() => {
    store.fetchData(params.id);
  }, []);

  if (!store.resData) return <></>
  console.log(store)
  return (
    <div>
        <header>
            <img src={store.resData.data.image.large}/>
            <h2>{store.resData.data.name}</h2>
        </header>
      <AreaChart
        width={500}
        height={400}
        data={store.graphData}
        margin={{
          top: 10,
          right: 30,
          left: 0,
          bottom: 0,
        }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="Date" />
        <YAxis />
        <Tooltip />
        <Area type="monotone" dataKey="Price" stroke="#8884d8" fill="#8884d8" />
      </AreaChart>
      <div>
        <h4>Market cap rank</h4>
        <span>{store.resData.data.market_cap_rank}</span>
      </div>
    </div>
  );
};
