import { Spin } from 'antd';

interface LoadingProps {
  tip?: string;
}

export const Loading: React.FC<LoadingProps> = ({ tip = 'Loading...' }) => {
  return (
    <div className="flex items-center justify-center min-h-[200px]">
      <Spin size="large" tip={tip} />
    </div>
  );
};
