import SortButton from '@/components/Button/SortButton';
import { searchApi } from '@/services/api-platform/apiController';
import { formatTimestamp } from '@/utils';
import { RedoOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProCard, ProList } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Button, Card, Popover, Row, Space, Typography, Image, Progress, Tag, Badge } from 'antd';
import Search from 'antd/lib/input/Search';
import React, { useRef, useState } from 'react';






const APIListPage: React.FC = () => {
  const apiSortFields = useRef<{
    title: string,
    name: string,
    sort?: string
  }[]>([
    {
      title: "价格",
      name: "price"
    },
    {
      title: "成交量",
      name: "orderVolume"
    },
    {
      title: "评分",
      name: "scope"
    }, {
      title: "创建时间",
      name: "ctime"
    }
  ]);
  const getAPISortReq = () => {
    const apiSortReq: API.APISortReq = {};
    apiSortFields.current.forEach(apiSortField => {
      if (apiSortField.sort !== undefined) {
        //@ts-ignore
        apiSortReq[apiSortField.name] = apiSortField.sort
      }
    });
    return apiSortReq;
  }



  const actionRef = useRef<ActionType>();
  const [current, setCurrent] = useState(1);
  const [params, setParams] = useState({ searchKey: "", sort: {} });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  // const [resultList, setResultList] = useState<API.ApiSearchVo[]>([]);
  // useEffect(() => {
  //   let load = true;
  //   searchApi({
  //     current,
  //     sort: getAPISortReq()
  //   }).then((data: API.RespPageRespApiSearchVo) => {
  //     if (load) {
  //       setResultList(data.data?.data!);
  //     }
  //   })
  //   return () => {
  //     load = false;
  //   }
  // }, []);

  return <>
    <PageContainer>
      <ProCard style={{ width: "100%" }}>
        <Space direction={"vertical"} style={{ width: "100%" }}>
          <ProCard >
            <Popover title="可以根据名称、描述搜索">
              <Search
                loading={isLoading}
                onSearch={async (key) => {
                  setIsLoading(true);
                  setParams({ searchKey: key, sort: getAPISortReq() });
                }} enterButton />
            </Popover>
          </ProCard>
          <ProCard >
            <Space direction={"horizontal"} style={{ width: "100%" }}>
              {
                apiSortFields.current.map(sortField => <SortButton sortField={sortField} />)
              }
            </Space>
          </ProCard >
        </Space>
      </ProCard >
      <Row justify={"end"}>
        <Button onClick={() => actionRef.current?.reload()}><RedoOutlined /></Button>
      </Row>
      <ProList<API.ApiSearchVo>
        params={params}
        actionRef={actionRef}
        pagination={{
          pageSize: 12,
          current: current
        }}
        request={async (params) => {
          try {
            const data = await searchApi({
              current: params.current,
              search: params.searchKey === "" ? undefined : params.searchKey,
              sort: params.sort
            });
            setCurrent(params.current!!);
            return {
              data: data.data?.data,
              success: true,
              total: data.data?.total,
            };
          } catch (e) {
            return {
              success: false,
            };
          } finally {
            setIsLoading(false);
          }
        }}
        grid={{ gutter: 16, column: 3 }}
        renderItem={(apiSearchVo: API.ApiSearchVo) => {
          return (
            <Link to={`/api/${apiSearchVo.id}`}>
              <Card
                style={{ margin: "10px" }}
              >
                <Image src={apiSearchVo.logoUrl} width={50} height={50} />
                <Typography.Title level={5}>
                  {apiSearchVo.name}
                </Typography.Title>
                <Space direction={"vertical"} style={{ width: "100%" }}>
                  <Space direction={"horizontal"} style={{ width: "100%" }}>
                    <Tag color="#5BD8A6">价格:{apiSearchVo.price}</Tag>
                    <Tag color="#5BD8A6">评分:{apiSearchVo.score}</Tag>
                    <Tag color="#5BD8A6">成交量:{apiSearchVo.orderVolume}</Tag>
                  </Space>
                  <Space direction={"vertical"} style={{ width: "100%" }}>

                    <Tag color="#5BD8A6">创建时间：{formatTimestamp(apiSearchVo.ctime)}</Tag>
                  </Space>
                  <Typography.Paragraph>
                    <Typography.Text>描述:
                    </Typography.Text>
                    {apiSearchVo.description}
                  </Typography.Paragraph>
                </Space>
              </Card>
            </Link>
          )
        }}
      />
    </PageContainer >
  </>
}
export default APIListPage;
