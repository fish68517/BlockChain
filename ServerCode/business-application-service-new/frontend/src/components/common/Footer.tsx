import { Layout } from 'antd';

const { Footer: AntFooter } = Layout;

export const Footer: React.FC = () => {
  return (
    <AntFooter className="text-center bg-gray-100">
      <p className="text-gray-500 text-sm">
        CollectorCoin &copy; 2026 - Blockchain Collectibles Platform
      </p>
    </AntFooter>
  );
};
