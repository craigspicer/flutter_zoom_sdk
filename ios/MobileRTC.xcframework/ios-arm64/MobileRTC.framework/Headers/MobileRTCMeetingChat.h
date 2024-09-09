//
//  MobileRTCMeetingChat.h
//  MobileRTC
//
//  Created by Zoom Video Communications on 2017/9/15.
//  Copyright © 2019年 Zoom Video Communications, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MobileRTC/MobileRTCConstants.h>

@interface MobileRTCFileTransferInfo : NSObject
@property(nonatomic, copy, nullable)    NSString *messageId;    /// the message identify of transfer file.
@property(nonatomic, assign)            MobileRTCFileTransferStatus transStatus; /// The status of the file transfer
@property(nonatomic, strong, nullable)  NSDate *timeStamp;      /// The time stamp of the file.
@property(nonatomic, assign)            BOOL isSendToAll;       /// Is the file send to all user in meeting?
@property(nonatomic, assign)            NSUInteger fileSize;    /// The bytes of transfer file size.
@property(nonatomic, copy, nullable)    NSString *fileName;     /// the file name of transfer file.
@property(nonatomic, assign)            NSUInteger completePercentage; /// The ratio of the file transfer completed
@property(nonatomic, assign)            NSUInteger completeSize;/// The size of the file transferred so far in bytes
@property(nonatomic, assign)            NSUInteger bitPerSecond;/// The speed of the file transfer in bits per second
@end


@interface MobileRTCFileSender : NSObject
@property(nonatomic, strong, nullable) MobileRTCFileTransferInfo *transferInfo; /// the basic information of transfer file.

/*!
 @brief Get file receiver's user id.
 @return The receiver user ID. -1 specify the internel error of get user ID. 0 specify the file send to all.
 */
- (NSInteger)getReceiverUserId;

/*!
 @brief Cancel the file send.
 @return The error type of the cancel action, For more details, see {@link MobileRTCSDKError}.
 */
- (MobileRTCSDKError)cancelSend;
@end

@interface MobileRTCFileReceiver : NSObject
@property(nonatomic, strong, nullable) MobileRTCFileTransferInfo *transferInfo; /// the basic information of transfer file.

/*!
 @brief Get file sender's user id .
 @return The sender user ID. -1 specify the internel error of get user ID.
 */
- (NSInteger)getSenderUserId;

/*!
 @brief Cancel the file receive.
 @return The error type of the cancel action. For more details, see {@link MobileRTCSDKError}.
 */
- (MobileRTCSDKError)cancelReceive;

/*!
 @brief Start receive the file.
 @param path The path to receive the file.
 @return The error type of the cancel action, For more details, see {@link MobileRTCSDKError}.
 */
- (MobileRTCSDKError)startReceive:(NSString * _Nullable)path;
@end


@class MobileRTCMeetingChat;
@class MobileRTCBoldAttrs;
@class MobileRTCItalicAttrs;
@class MobileRTCStrikethroughAttrs;
@class MobileRTCBulletedListAttrs;
@class MobileRTCNumberedListAttrs;
@class MobileRTCUnderlineAttrs;
@class MobileRTCQuoteAttrs;
@class MobileRTCInsertLinkAttrs;
@class MobileRTCFontSizeAttrs;
@class MobileRTCFontColorAttrs;
@class MobileRTCBackgroundColorAttrs;
@class MobileRTCParagraphAttrs;
@class MobileRTCIndentAttrs;
/*!
 @brief Chat message builder to create ChatMsgInfo objects.
 Tips: If there are duplicate styles, the final appearance is determined by the last applied setting.
 */
@interface MobileRTCMeetingChatBuilder : NSObject
/*!
 @brief Set chat message content.
 @param content The chat message’s content.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setContent:(NSString * _Nullable)content;
/*!
 @brief Set who will receive the chat message.
 @param receiver Specify the user ID to receive the chat message. The message is sent to all participants when the value is zero (0).
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setReceiver:(NSInteger)receiver;
/*!
 @brief Set the ID of the thread where the message will be posted.
 @param threadId Specify the thread ID.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setThreadId:(NSString * _Nullable)threadId;
/*!
 @brief Set the chat message type.
 @param type The chat message’s type.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setMessageType:(MobileRTCChatMessageType)type;

/*!
 @brief Set the chat message content quote style.
 @param start The segment start position.
 @param end The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setQuotePosition:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content quote style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetQuotePosition:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content italic style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setItalic:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content italic style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetItalic:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content bold style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setBold:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content bold style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetBold:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content strikethrough style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setStrikethrough:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content strikethrough style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetStrikethrough:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content bulletedList style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setBulletedList:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content bulletedList style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetBulletedList:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content numberedList style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setNumberedList:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content numberedList style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetNumberedList:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content underline style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setUnderline:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content underline style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetUnderline:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content font size style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setFontSize:(MobileRTCFontSizeAttrs * _Nullable)size start:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content font size style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetFontSize:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content insert link style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setInsertLink:(MobileRTCInsertLinkAttrs * _Nullable)link start:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content insert link style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetInsertLink:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content background color style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setBackgroundColor:(MobileRTCBackgroundColorAttrs * _Nullable)color start:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content background color style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetBackgroundColor:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content font color style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setFontColor:(MobileRTCFontColorAttrs * _Nullable)color start:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content font color style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetFontColor:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Increase the chat message content indent style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)increaseIndent:(MobileRTCIndentAttrs * _Nullable)indent start:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Decrease the chat message content indent style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)decreaseIndent:(MobileRTCIndentAttrs * _Nullable)indent start:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Set the chat message content paragraph style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)setParagraph:(MobileRTCParagraphAttrs * _Nullable)paragraph start:(NSInteger)start end:(NSInteger)end;
/*!
 @brief Clear the chat message content paragraph style.
 @param start The segment start position.
 @param end  The segment end position.
 @return Return the MobileRTCMeetingChatBuilder object with modify information.
 */
- (MobileRTCMeetingChatBuilder * _Nullable)unsetParagraph:(NSInteger)start end:(NSInteger)end;

/*!
 @brief Build chat message entity.
 @return If the function succeeds, the return value is the message detail information.
 */
- (MobileRTCMeetingChat * _Nullable)build;

@end

/*!
 @brief for rich text solution, the specify rich text offset information.
 @deprecated This class is deprecated, use MobileRTCSegmentDetails instead.
 */
__attribute__((deprecated("This class is deprecated, use MobileRTCSegmentDetails instead")))
@interface MobileRTCRichTextStyleOffset : NSObject
/*!
 @brief Get a certain rich-text style’s start position.
 */
@property (nonatomic, assign) NSInteger positionStart;
/*!
 @brief Get a certain rich-text style’s end position.
 */
@property (nonatomic, assign) NSInteger positionEnd;
/*!
 @brief Get a certain rich-text style’s supplementary information.
 @warning If the style is TextStyle_FontSize, possible return values are FontSize_Small, FontSize_Medium, or FontSize_Large.
 If style is TextStyle_Paragraph, possible return values are as follows Paragraph_H1, Paragraph_H2, or Paragraph_H3.
 If the style is TextStyle_FontColor, or TextStyle_BackgroundColor, possible return values are hex string representing standard RGB data.
 */
@property (nonatomic, copy) NSString * _Nullable reserve;
@end

/*!
 @brief for rich text solution, the specify text styple information.
 @deprecated This class is deprecated, use MobileRTCSegmentDetails instead.
 */
__attribute__((deprecated("This class is deprecated, use MobileRTCSegmentDetails instead")))
@interface MobileRTCRichTextStyleItem : NSObject
/*!
 @brief Get the rich text type of a portion of the current message.
 */
@property (nonatomic, assign) MobileRTCRichTextStyle textStyle;
/*!
 @brief Get the current message’s rich text position info list of a certain style.
 */
@property (nonatomic, copy) NSArray <MobileRTCRichTextStyleOffset *>* _Nullable textStyleOffsetList;
@end


#pragma mark - new chat -
/*!
 @brief Information of rich text with style attributes in a chat message content. Here are more detailed structural descriptions.
*/
@interface MobileRTCSegmentDetails : NSObject
@property (nonatomic, copy) NSString * _Nullable content;                               // Segment content value.

@property (nonatomic, strong) MobileRTCBoldAttrs * _Nullable boldAttrs;                 // Segment BoldAttrs value.
@property (nonatomic, strong) MobileRTCItalicAttrs * _Nullable italicAttrs;             // Segment ItalicAttrs value.
@property (nonatomic, strong) MobileRTCStrikethroughAttrs * _Nullable strikethroughAttrs;   // StrikethroughAttrs content value.
@property (nonatomic, strong) MobileRTCBulletedListAttrs * _Nullable bulletedListAttrs; // Segment BulletedListAttrs value.
@property (nonatomic, strong) MobileRTCNumberedListAttrs * _Nullable numberedListAttrs; // Segment NumberedListAttrs value.
@property (nonatomic, strong) MobileRTCUnderlineAttrs * _Nullable underlineAttrs;       // Segment UnderlineAttrs value.
@property (nonatomic, strong) MobileRTCQuoteAttrs * _Nullable quoteAttrs;               // Segment QuoteAttrs value.
@property (nonatomic, strong) MobileRTCInsertLinkAttrs * _Nullable insertLinkAttrs;     // Segment InsertLinkAttrs value.
@property (nonatomic, strong) MobileRTCFontSizeAttrs * _Nullable fontSizeAttrs;         // Segment FontSizeAttrs value.
@property (nonatomic, strong) MobileRTCFontColorAttrs * _Nullable fontColorAttrs;       // Segment FontColorAttrs value.
@property (nonatomic, strong) MobileRTCBackgroundColorAttrs * _Nullable backgroundColorAttrs;// Segment BackgroundColorAttrs value.
@property (nonatomic, strong) MobileRTCParagraphAttrs * _Nullable paragraphAttrs;       // Segment ParagraphAttrs value.
@property (nonatomic, strong) MobileRTCIndentAttrs * _Nullable indentAttrs;             // Segment IndentAttrs value.
@end

/*!
@brief Bold attributes.
*/
@interface MobileRTCBoldAttrs : NSObject
@property (nonatomic, assign) BOOL bBold; //If the value is YES, the text style is Bold.
@end

/*!
@brief Italic attributes.
*/
@interface MobileRTCItalicAttrs : NSObject
@property (nonatomic, assign) BOOL bItalic; //If the value is true, the text style is Italic.
@end

/*!
@brief Strikethrough attributes.
*/
@interface MobileRTCStrikethroughAttrs : NSObject
@property (nonatomic, assign) BOOL bStrikethrough; //If the value is true, the text style is Strikethrough.
@end

/*!
@brief BulletedList attributes.
*/
@interface MobileRTCBulletedListAttrs : NSObject
@property (nonatomic, assign) BOOL bBulletedList; //If the value is true, the text style is BulletedList.
@end

/*!
@brief NumberedList attributes.
*/
@interface MobileRTCNumberedListAttrs : NSObject
@property (nonatomic, assign) BOOL bNumberedList; //If the value is true, the text style is Numbered.
@end

/*!
@brief Underline attributes.
*/
@interface MobileRTCUnderlineAttrs : NSObject
@property (nonatomic, assign) BOOL bUnderline; //If the value is true, the text style is Underline.
@end

/*!
@brief Quote attributes.
*/
@interface MobileRTCQuoteAttrs : NSObject
@property (nonatomic, assign) BOOL bQuote; //If the value is true, the text style is Quote.
@end

/*!
@brief InsertLink attributes.
*/
@interface MobileRTCInsertLinkAttrs : NSObject
@property (nonatomic, copy) NSString * _Nullable insertLinkUrl; //If insertLinkUrl is not empty, the text style has insert link url.
@end

/*!
@brief FontSize attributes. Currently supported font size value as following.
*/
#define FontSize_Small 8
#define FontSize_Medium 10
#define FontSize_Large 12
@interface MobileRTCFontSizeAttrs : NSObject
@property (nonatomic, assign) NSInteger fontSize; // Font size value. default is FontSize_Medium
@end


/*!
 @brief FontColor attributes.
 @warning Currently supported font color combinations.
 * FontColor_Red, 235,24,7
 * FontColor_Orange, 255,138,0
 * FontColor_Yellow, 248,194,0
 * FontColor_Green, 19,138,0
 * FontColor_Blue, 0,111,250
 * FontColor_Violet, 152,70,255
 * FontColor_Rosered, 226,0,148
 * FontColor_Black 34,34,48
 */
#define kRichTextColor(r,g,b) [UIColor colorWithRed:r/255. green:g/255. blue:b/255. alpha:1.]
@interface MobileRTCFontColorAttrs : NSObject
@property (nonatomic, strong) UIColor * _Nullable color; // Font color
@end

/*!
 @brief Background color attributes.
 @warning Currently supported background color combinations.
 * BackgroundColor_Normal, 255,255,255
 * BackgroundColor_Red, 255,67,67
 * BackgroundColor_Orange, 255,138,0
 * BackgroundColor_Yellow, 255,214,0
 * BackgroundColor_Green, 73,214,30
 * BackgroundColor_Blue, 47,139,255
 * BackgroundColor_Violet, 171,104,255
 * BackgroundColor_Rosered, 255,54,199
 * BackgroundColor_White 255,255,255
 */
@interface MobileRTCBackgroundColorAttrs : NSObject
@property (nonatomic, strong) UIColor * _Nullable color; // Back ground color
@end

/**
 * The attribute strParagraph can be set with value below.
 * If need customized paragraph, fill strParagraph with hope.
 */
#define TextStyle_Paragraph_H1      @"Paragraph_H1"
#define TextStyle_Paragraph_H2      @"Paragraph_H2"
#define TextStyle_Paragraph_H3      @"Paragraph_H3"
 
/*!
 @brief Paragraph attributes..
 */
@interface MobileRTCParagraphAttrs : NSObject
@property (nonatomic, copy) NSString * _Nullable strParagraph; //If strParagraph is not empty, text style has Paragraph style.
@end

/*!
 @brief Indent attributes..
 */
@interface MobileRTCIndentAttrs : NSObject
@property (nonatomic, assign) NSInteger indent; // The number of times the indentation style is applied.
@end


/*!
 @brief Retrieve the meeting chat data.
 @warning The function is optional.
 */
@interface MobileRTCMeetingChat : NSObject

/*!
 @brief The message ID.
 */
@property (nonatomic, copy) NSString * _Nullable chatId;
/*!
 @brief The ID of user who sends message.
 */
@property (nonatomic, copy) NSString * _Nullable senderId;
/*!
 @brief The screen name of user who sends message.
 */
@property (nonatomic, copy) NSString * _Nullable senderName;
/*!
 @brief The ID of user who receives message.
 */
@property (nonatomic, copy) NSString * _Nullable receiverId;
/*!
 @brief The screen name of user who receives message.
 */
@property (nonatomic, copy) NSString * _Nullable receiverName;
/*!
 @brief The message content.
 */
@property (nonatomic, copy) NSString * _Nullable content;
/*!
 @brief The message timestamps.
 */
@property (nonatomic, retain) NSDate *_Nullable date;

/*!
 @brief The Chat message type.
 */
@property (nonatomic, assign) MobileRTCChatMessageType chatMessageType;

/*!
 @brief Whether the message is sent by the user himself or not.
 */
@property (nonatomic, assign) BOOL isMyself;
/*!
 @brief Whether the message is private or not.
 */
@property (nonatomic, assign) BOOL isPrivate;
/*!
 @brief Whether the message is send to all or not.
 */
@property (nonatomic, assign) BOOL isChatToAll;
/*!
 @brief Whether the message is send to all panelist or not.
 */
@property (nonatomic, assign) BOOL isChatToAllPanelist;
/*!
 @brief Whether the message is send to waiting room or not.
 */
@property (nonatomic, assign) BOOL isChatToWaitingroom;
/*!
@brief Determine if the current message is a comment replying to another message.
 */
@property (nonatomic, assign) BOOL isComment;
/*!
@brief Determine if the current message is part of a message thread. and can be directly replied to.
*/
@property (nonatomic, assign) BOOL isThread;
/*!
@brief Get the current message’s chat message font style list.
*/
@property (nonatomic, copy) NSArray <MobileRTCRichTextStyleItem *>* _Nullable textStyleItemList DEPRECATED_MSG_ATTRIBUTE("Use segmentDetails instead");

/*!
@brief Get the chat message segment content and style detail of the current message.
* When receiving rich-text messages, a list of isolated paragraphs is included,
* each formatted according to its style. Concatenating these paragraphs
* together forms the complete message text.
*/
@property (nonatomic, copy) NSArray <MobileRTCSegmentDetails *>* _Nullable segmentDetails;
/*!
@brief Get the current message’s thread ID.
*/
@property (nonatomic, copy) NSString * _Nullable threadID;
@end
