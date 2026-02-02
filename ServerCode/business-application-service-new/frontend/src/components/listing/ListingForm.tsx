import { useState } from 'react';
import { Form, Input, Button, Upload, Radio, Space, Image, App, InputNumber } from 'antd';
import { UploadOutlined, LinkOutlined, PictureOutlined } from '@ant-design/icons';
import type { UploadFile, UploadProps } from 'antd';
import type { CreateListingRequest } from '@/types';
import { uploadImage } from '@/services/listingService';

const { TextArea } = Input;

interface ListingFormProps {
  onSubmit: (values: CreateListingRequest) => Promise<void>;
  loading?: boolean;
}

export const ListingForm: React.FC<ListingFormProps> = ({ onSubmit, loading }) => {
  const [form] = Form.useForm();
  const [imageMode, setImageMode] = useState<'url' | 'upload'>('upload');
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [previewUrl, setPreviewUrl] = useState<string>('');
  const [, setUploading] = useState(false);
  const [uploadedUrl, setUploadedUrl] = useState<string>('');
  const { message } = App.useApp();

  const handleFinish = async (values: CreateListingRequest) => {
    try {
      // If using upload mode and file was uploaded
      if (imageMode === 'upload' && uploadedUrl) {
        values.imageUrl = uploadedUrl;
      }
      
      await onSubmit(values);
      form.resetFields();
      setFileList([]);
      setPreviewUrl('');
      setUploadedUrl('');
      message.success('Project created successfully!');
    } catch {
      message.error('Failed to create project');
    }
  };

  const uploadProps: UploadProps = {
    listType: 'picture-card',
    fileList,
    maxCount: 1,
    accept: 'image/*',
    customRequest: async ({ file, onSuccess, onError }) => {
      try {
        setUploading(true);
        const url = await uploadImage(file as File);
        setUploadedUrl(url);
        setPreviewUrl(url);
        onSuccess?.({ url });
        message.success('Image uploaded successfully');
      } catch (err) {
        onError?.(err as Error);
        message.error('Failed to upload image');
      } finally {
        setUploading(false);
      }
    },
    onChange: ({ fileList: newFileList }) => {
      setFileList(newFileList);
      if (newFileList.length === 0) {
        setPreviewUrl('');
        setUploadedUrl('');
      }
    },
    onRemove: () => {
      setPreviewUrl('');
      setUploadedUrl('');
    },
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleFinish}
      className="max-w-2xl"
    >
      <Form.Item
        name="title"
        label="Project Title"
        rules={[{ required: true, message: 'Please enter a title' }]}
      >
        <Input placeholder="Enter project title" size="large" />
      </Form.Item>

      <Form.Item
        name="description"
        label="Description"
      >
        <TextArea rows={4} placeholder="Describe the collectible item" />
      </Form.Item>

      <Form.Item
        name="vin"
        label="VIN (Vehicle Identification Number)"
      >
        <Input placeholder="Enter VIN if applicable" />
      </Form.Item>

      <Form.Item
        name="ownerValueEstimation"
        label="Estimated Final Value (CCT)"
        rules={[{ required: true, message: 'Please enter your estimated final value' }]}
      >
        <InputNumber
          placeholder="Enter your estimated final value"
          min={0}
          step={0.01}
          style={{ width: '100%' }}
          size="large"
        />
      </Form.Item>

      <Form.Item
        name="ownerRepairEstimation"
        label="Estimated Repair Cost (CCT)"
        rules={[{ required: true, message: 'Please enter your estimated repair cost' }]}
      >
        <InputNumber
          placeholder="Enter your estimated repair cost"
          min={0}
          step={0.01}
          style={{ width: '100%' }}
          size="large"
        />
      </Form.Item>

      <Form.Item label="Project Image">
        <Radio.Group 
          value={imageMode} 
          onChange={(e) => setImageMode(e.target.value)}
          style={{ marginBottom: 16 }}
        >
          <Radio.Button value="upload">
            <Space><UploadOutlined /> Local Upload</Space>
          </Radio.Button>
          <Radio.Button value="url">
            <Space><LinkOutlined /> URL</Space>
          </Radio.Button>
        </Radio.Group>

        {imageMode === 'upload' ? (
          <Upload {...uploadProps}>
            {fileList.length === 0 && (
              <div style={{ padding: 20 }}>
                <PictureOutlined style={{ fontSize: 32, color: '#3b82f6' }} />
                <div style={{ marginTop: 8, color: '#64748b' }}>Click or drag to upload</div>
              </div>
            )}
          </Upload>
        ) : (
          <Form.Item name="imageUrl" noStyle>
            <Input 
              placeholder="Enter image URL or IPFS hash" 
              onChange={(e) => setPreviewUrl(e.target.value)}
            />
          </Form.Item>
        )}

        {previewUrl && imageMode === 'url' && (
          <div style={{ marginTop: 16 }}>
            <Image
              src={previewUrl}
              alt="Preview"
              style={{ maxWidth: 200, borderRadius: 8 }}
              fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
            />
          </div>
        )}
      </Form.Item>

      <Form.Item>
        <Button 
          type="primary" 
          htmlType="submit" 
          loading={loading} 
          size="large"
          style={{ 
            background: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
            border: 'none',
            height: 48,
            paddingInline: 32,
            fontWeight: 500,
          }}
        >
          Create Project
        </Button>
      </Form.Item>
    </Form>
  );
};
