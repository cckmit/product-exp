<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="customroot">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simple">
                    <fo:region-body region-name="xsl-region-body" margin-top="3.0cm" margin-bottom="3.0cm"
                                    margin-left="2.0cm" margin-right="2.0cm"/>
                    <fo:region-before region-name="xsl-region-before"/>
                    <fo:region-after region-name="xsl-region-after" extent="4.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simple">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table width="100%" margin-top="1.0cm" margin-left="0.5cm" margin-right="1.5cm">
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-column column-width="50%" text-align="right"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-align="left">
                                        <fo:external-graphic src="tmblogo.png" content-width="2.00in"
                                                             scaling="non-uniform"/>
                                    </fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block margin-top="0.7cm">
                                        <fo:external-graphic src="makerealchange.png" content-width="1.2in"
                                                             scaling="non-uniform"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:table width="100%" margin-left="0.5cm" margin-right="1.5cm">
                        <fo:table-column column-width="100%" text-align="left"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-align="left">
                                        <fo:external-graphic src="tmbaddress.png" content-width="7.00in"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX" color="#333333"
                              font-size="13pt" font-weight="normal" empty-cells="show">
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-column column-width="50%" text-align="right"/>

                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" font-size="11pt" number-columns-spanned="2">
                                    <fo:block background-color="#C0C0C0" border="solid 0.2mm black">
                                        <fo:block margin-left="0.5cm">รายละเอียดการขอสินเชื่อ</fo:block>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                        </fo:table-body>
                    </fo:table>


                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX"
                              color="#333333" font-size="13pt" font-weight="normal" empty-cells="show"
                              start-indent="0cm" margin-top="-1.0cm">
                        <fo:table-column column-width="50%" text-align="right"/>
                        <fo:table-column column-width="50%" text-align="left"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>ใบสมัครเลขที่ :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="appRefNo"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>ผลิตภัณฑ์ที่ต้องการสมัคร :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="productName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>ชื่อ - นามสกุล :</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="customerName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>เลขที่บัตรประชาชน :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="idCardNo"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="productCode='C2G01' or productCode='C2G02'">
                                                วงเงินกู้ :
                                            </xsl:when>
                                            <xsl:otherwise>
                                                วงเงินบัตร :
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="finalLoanAmount"/> บาท
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="productCode='RC01'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ระยะเวลาการผ่อนชำระ :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="tenor"/> เดือน
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell text-align="left" number-columns-spanned="2">
                                        <fo:block>
                                            <fo:leader leader-pattern="space"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='RC01' or productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>อัตราดอกเบี้ย :</fo:block>
                                    </fo:table-cell>
                                    <xsl:if test="productCode='RC01'">
                                        <fo:table-cell text-align="right">
                                            <fo:block>
                                                <xsl:value-of select="interestRate"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </xsl:if>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G01'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>เดือนที่ 1 - เดือนที่ 12 เท่ากับ CPR 18%</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>เดือนท่ี 13 - เดือนที่ 24 เท่ากับ CPR 18%</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ปัจจุบัน CRP = 18%</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell text-align="left" number-columns-spanned="2">
                                        <fo:block>
                                            <fo:leader leader-pattern="space"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                            </xsl:if>

                            <xsl:if test="productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>เดือนท่ี 1 - เดือนที่ 12 เท่ากับ CPR 18%</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ปัจจุบัน CRP = 18%</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell text-align="left" number-columns-spanned="2">
                                        <fo:block>
                                            <fo:leader leader-pattern="space"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ยอดเงินที่จะได้รับโอนเข้าบัญชี :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="cashDisbursement"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>บัญชีรับเงิน :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="disburseAccountNo"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ระยะเวลาผ่อนชำระตามสัญญา :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="tenor"/> เดือน
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                            </xsl:if>

                            <xsl:if test="productCode='C2G02'">
                                <fo:table-row>

                                    <fo:table-cell>
                                        <fo:block>ยอดเงินกู้ที่นำไปชำระหนี้เดิม :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="currentLoan"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>บัญชีเงินกู้เลขที่ :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="currentAccount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                            </xsl:if>

                            <xsl:if test="productCode='RC01'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>จำนวนเงินที่ต้องการขอเบิกใช้ :</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="requestAmount"/> บาท
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>วันที่ทำสัญญา :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="applyDate"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>วันครบรอบกำหนดชำระ :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="dueDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>เริ่มชำระงวดแรก :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="firstPaymentDueDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>รอบชำระเงินถัดไป :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="nextPaymentDueDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>ยอดเงินท่ีต้องชำระรายเดือน :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="installment"/> บาท
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="productCode='C2G01' or productCode='C2G02'">
                                                วิธีการผ่อนชำระเงิน :
                                            </xsl:when>
                                            <xsl:otherwise>
                                                วิธีการชำระเงิน :
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="paymentMethod='1'">
                                                หักจากบัญชีธนาคาร
                                            </xsl:when>
                                            <xsl:otherwise>
                                                เงินสด
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>Estatement :</fo:block>
                                </fo:table-cell>

                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:value-of select="email"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="productCode='RC01' or productCode='C2G01' or productCode='C2G02'">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>สินเชื่อฯ กับสถาบันการเงินอื่น :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="botAnswer1"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>สินเชื่อฯ ที่อยู่ระหว่างการพิจารณา :</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="botAnswer2"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                            <fo:table-row>
                                <fo:table-cell text-align="left" number-columns-spanned="2">
                                    <fo:block>
                                        <fo:leader leader-pattern="space"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                        </fo:table-body>
                    </fo:table>

                    <fo:table width="100%" vertical-align="middle" font-family="DBOzoneX" color="#333333"
                              font-size="13pt" font-weight="normal" empty-cells="show">
                        <fo:table-body>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ข้าพเจ้าขอยืนยันต่อธนาคารว่าข้อมูลที่ข้าพเจ้าให้ไว้ต่อธนาคารเป็นความจริงทุกประการและข้าพเจ้าอนุญาตให้ธนาคารเรียก
                                    </fo:block>
                                    <fo:block>
                                        หลักฐานการเงินเพิ่มเติมเพื่อวัตถุประสงค์ในการประเมินวงเงินสินเชื่อ
                                        ข้าพเจ้ารับทราบว่าการสมัครของข้าพเจ้าจะเป็นที่ยอมรับได้ต่อเมื่อ
                                    </fo:block>
                                    <fo:block>
                                        ได้รับการอนุมัติเป็นลายลักษณ์อักษรจากกธนาคารแล้ว
                                        โดยข้าพเจ้ายินยอมผูกพันและตกลงปฏิบัติตามข้อกำหนดและเงื่อนไขของสินเชื่อ
                                    </fo:block>
                                    <fo:block>
                                        บุคคล<xsl:value-of select="productName"/>ระบุไว้ทุกประการ
                                        รวมตลอดถึงข้อกำหนดและเงื่อนไขเพิ่มเติมที่ธนาคารจะได้แจ้งให้ทราบเป็นครั้งคราว
                                        ในกรณีที่ธนาคารไม่
                                    </fo:block>
                                    <fo:block>
                                        สามารถอนุมัติจำนวนเงินกู้และระยะเวลาการผ่อนชำระคืนได้ตามความประสงค์ของข้าพเจ้าที่ระบุในคำขอนี้
                                        ข้าพเจ้ายังประสงค์และตกลง
                                    </fo:block>
                                    <fo:block>
                                        ผูกพันตามจำนวนเงินกู้และระยะเวลาการผ่อนชำระคืน
                                        ตลอดจนข้อกำหนดและเงื่อนไขที่ธนาคารได้อนุมัติ ให้ข้าพเจ้าทุกประการ ข้าพเจ้า
                                    </fo:block>
                                    <fo:block>
                                        ได้อ่านและเข้าใจข้อกำหนดและเงื่อนไขที่เกี่ยวข้องอย่างถี่ถ้วนแล้วจึงลงลายมือชื่ออิเล็กทรอนิกส์ไว้เป็นหลักฐานแห่งการกู้ยืม
                                        และข้าพเจ้า
                                    </fo:block>
                                    <fo:block>
                                        รับทราบว่าธนาคารจะจัดส่งสำเนาสัญญาสินเชื่อให้แก่ข้าพเจ้าผ่านทางจดหมายอิเล็กทรอนิกส์
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ข้าพเจ้าขอรับรองว่าข้อมูลที่ให้ไว้ในการสมัครสินเชื่อนี้เป็นความจริงทุกประการและตกลงให้บริษัทข้อมูลเครดิตแห่งชาติ
                                        จำกัด
                                    </fo:block>
                                    <fo:block>
                                        (บริษัท) เปิดเผยหรือให้ข้อมูลของข้าพเจ้าแก่ธนาคารทหารไทย จำกัด (มหาชน)
                                        ซึ่งเป็นสมาชิกหรือผู้ใช้บริการของบริษัทเพื่อประโยชน์ใน
                                    </fo:block>
                                    <fo:block>
                                        การวิเคราะห์สินเชื่อ การออกบัตรเครดิต
                                        ตามคำขอสินเชื่อ/ขอออกบัตรเครดิตของข้าพเจ้า
                                        รวมทั้งเพื่อประโยชน์ในการทบทวนสินเชื่อต่อ
                                    </fo:block>
                                    <fo:block>
                                        อายุสัญญาสินเชื่อ/บัตรเครดิต
                                        การบริหารและป้องกันความเสี่ยงตามข้อกำหนดของธนาคารแห่งประเทศไทย
                                        และให้ถือว่าคู่ฉบับ และ
                                    </fo:block>
                                    <fo:block>
                                        บรรดาสำเนาภาพถ่าย
                                        ข้อมูลอิเล็กทรอนิกส์หรือโทรสารที่ทำสำเนาขึ้นจากหนังสือให้ความยินยอมฉบับนี้
                                        โดยการถ่ายสำเนา ถ่ายภาพ
                                    </fo:block>
                                    <fo:block>
                                        หรือบันทึกไว้ ไม่ว่าในรูปแบบใดๆ
                                        เป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่นเดียวกัน จนกว่าข้าพเจ้าจะแจ้งยกเลิก
                                        หรือเพิกถอน
                                    </fo:block>
                                    <fo:block>
                                        ความยินยอมนี้ เป็นลายลักษณ์อักษรต่อธนาคาร
                                        รวมถึงยอมรับผูกพันปฏิบัติตามข้อกำหนดและเงื่อนไขการใช้บัตรเครดิตของธนาคาร
                                    </fo:block>
                                    <fo:block>
                                        หรือ กฏระเบียบ ข้อบังคับ การเป็นสมาชิกบัตรเครดิตของธนาคารที่ได้รับ
                                        พร้อมบัตรเครดิตของธนาคาร รวมทั้งที่แก้ไขเพิ่มเติมต่อไป
                                    </fo:block>
                                    <fo:block>
                                        ด้วย
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:if test="paymentMethod='1'">
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-indent="1cm">
                                            กรณีที่ข้าพเจ้าเลือก ชำระด้วยการหักบัญชีเงินฝาก TMB บนใบสมัครนี้ หมายถึง
                                            ยินยอมให้ธนาคารหักเงินฝากจากบัญชีเงิน
                                        </fo:block>
                                        <fo:block>
                                            ฝากออมทรัพย์ เลขที่
                                            <xsl:value-of select="disburseAccountNo"/> [TMB Direct Debit Account value]
                                            ดังกล่าวเพื่อชำระหนี้หรือ ค่าธรรมเนียมหรือค่าใช้จ่าย
                                        </fo:block>
                                        <fo:block>
                                            อื่นใดที่เกิดจากการใช้บริการสินเชื่อ โดยไม่ต้องขอความยินยอมจากข้าพเจ้า
                                            และธนาคารไม่จำเป็นต้องแจ้งการหักบัญชีแก่ข้าพเจ้าแต่
                                        </fo:block>
                                        <fo:block>
                                            อย่างใด
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-indent="1cm">
                                            ผู้สมัครให้ความยินยอมโดย :
                                            <xsl:value-of select="customerName"/>
                                        </fo:block>

                                        <fo:block text-indent="1cm">
                                            วันเวลาที่ให้ความยินยอม
                                            <xsl:value-of select="consentDate"/>
                                            <!--                                            / <xsl:value-of select="consentTime"/>-->
                                        </fo:block>
                                        <fo:block>
                                            <fo:leader leader-pattern="space"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:if>
                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        การให้ความยินยอมจากเจ้าของข้อมูลเพื่อใช้จัดทำแบบจำลองด้านเครดิต
                                        <xsl:choose>
                                            <xsl:when test="ncbConsentFlag='Y'">
                                                ยินยอม
                                            </xsl:when>
                                            <xsl:otherwise>
                                                ไม่ยินยอม
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        ให้ ธนาคารทหารไทย จำกัด (มหาชน) ซึ่ง
                                    </fo:block>
                                    <fo:block>เป็นสมาชิกของบริษัทข้อมูลเครดิต
                                        นำข้อมูลของข้าพเจ้าที่ได้รับจากบริษัทข้อมูลเครดิตเฉพาะส่วนที่ ไม่สามารถระบุตัวตน
                                        เช่น ชื่อ
                                    </fo:block>
                                    <fo:block>นามสกุล เลขประจำตัวประชาชน
                                        รวมถึงข้อมูลอื่นใดที่สามารถระบุว่าเป็นข้าพเจ้าได้ไปใช้เป็นปัจจัยหนึ่งในการจัดทำแบบจำลองด้าน
                                    </fo:block>
                                    <fo:block>เครดิตกำหนดเท่านั้น และให้คู่ฉบับและบรรดาสำเนาภาพถ่าย ข้อมูลอิเล็กทรอนิกส์
                                        หรือ โทรสาร ที่ได้ทำซ้ำขึ้นจากหนังสือให้ความ
                                    </fo:block>
                                    <fo:block>ยินยอมฉบับนี้ โดยการถ่ายสำเนาภาพถ่าย หรือบันทึกไว้ไม่ว่าในรูปแบบใดๆ
                                        ให้ถือเป็นหลักฐานในการให้ความยินยอมของข้าพเจ้าเช่น
                                    </fo:block>
                                    <fo:block>เดียวกัน ทั้งนี้
                                        ข้าพเจ้าทราบว่าเจ้าของข้อมูลมีสิทธิที่จะให้ความยินยอมหรือไม่ก็ได้
                                        และเมื่อให้ความยินยอมแล้ว เจ้าของข้อมูลจะแจ้ง
                                    </fo:block>
                                    <fo:block>ความประสงค์ไม่ให้ความยินยอมอีกต่อไปได้</fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        ผู้สมัครให้ความยินยอมโดย :
                                        <xsl:value-of select="customerName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell text-align="left">
                                    <fo:block text-indent="1cm">
                                        วันเวลาที่ให้ความยินยอม
                                        <xsl:value-of select="consentDate"/>
                                        <!--                                            / <xsl:value-of select="consentTime"/>-->
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
